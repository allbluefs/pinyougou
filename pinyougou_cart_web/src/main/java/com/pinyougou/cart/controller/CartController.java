package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.cart.service.CartService;
import entity.Result;
import groupEntity.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import utils.CookieUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * @Date:2019/7/23
 */
@RestController
@RequestMapping("/cart")
public class CartController {
    @Reference(timeout = 6000)
    private CartService cartService;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private HttpServletResponse response;
    @Autowired
    private HttpSession session;

    private String getSessionId(){
        String sessionId = CookieUtil.getCookieValue(request, "cartCookie", "utf-8");
        if (sessionId == null) {
            sessionId=session.getId();
            CookieUtil.setCookie(request,response,"cartCookie",sessionId,3600*24*7,"utf-8");
        }
        return sessionId;
    }

    @RequestMapping("/findCartList")
    public List<Cart> findCartList(){
        String sessionId = getSessionId();
        List<Cart> cartList_sessionId = cartService.findCartListFromRedis(sessionId);
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!username.equals("anonymousUser")){
            System.out.println("select cartList by username");
            List<Cart> cartList_username = cartService.findCartListFromRedis(username);
            if (cartList_sessionId != null && cartList_sessionId.size() > 0) {
                cartList_username=cartService.mergeCartList(cartList_sessionId,cartList_username);
                cartService.deleteCartList(sessionId);
                cartService.addCartListToRedisByUserName(username,cartList_username);
            }
            return cartList_username;
        }else {
            System.out.println("select cartList by sessionId");
            return cartList_sessionId;
        }


    }

    @RequestMapping("/addGoodsToCartList")
    @CrossOrigin(origins = "http://item.pinyougou.com",allowCredentials = "true")
    public Result addGoodsToCartList(Long itemId,Integer num){
        String sessionId = getSessionId();
        try {
            List<Cart> cartList = findCartList();
            cartList=cartService.addItemToCartList(cartList,itemId,num);

            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            if (!username.equals("anonymousUser")){
               cartService.addCartListToRedisByUserName(username,cartList);
            }else {
                cartService.addCartListToRedis(sessionId,cartList);
            }
            return new Result(true,"添加成功");
        } catch (RuntimeException e) {
            e.printStackTrace();
            return new Result(false,e.getMessage());
        }catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"添加失败");
        }
    }


}
