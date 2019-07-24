package com.pinyougou.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbOrderItem;
import groupEntity.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Date:2019/7/23
 */
@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private TbItemMapper itemMapper;

    //添加商品到购物车
    @Override
    public List<Cart> addItemToCartList(List<Cart> cartList, Long itemId, Integer num) {
        //根据itemId查询item
        TbItem item = itemMapper.selectByPrimaryKey(itemId);
        //判断item是否存在
        if (item == null) {
            throw new RuntimeException("商品不存在");
        }
        //判断商品状态是否为上架状态
        if (!item.getStatus().equals("1")) {
            throw new RuntimeException("商品已失效");
        }
        //1.判断购物车列表中是否有该商家的店铺
        Cart cart = searchCartFromCartList(cartList, item.getSellerId());

        if (cart != null) {//2.购物车列表中有该商家购物车
            List<TbOrderItem> orderItemList = cart.getOrderItemList();
            //2.1.该商家购物车中有该商品
            TbOrderItem orderItem = searchOrderItemFromCart(orderItemList, item);
            if (orderItem != null) {
                //商品数量增加,费用重新计算
                orderItem.setNum(orderItem.getNum() + num);
                orderItem.setTotalFee(new BigDecimal(orderItem.getPrice().doubleValue()*orderItem.getNum()));
                //2.3如果商品明细的数量<=0,将商品明细从商品明细列表中删除
                if (orderItem.getNum() <= 0) {
                    orderItemList.remove(orderItem);
                }
                //如果该商家购物车中商品明细列表的size为0 那么将购物车删除
                if (orderItemList.size() == 0) {
                    cartList.remove(cart);
                }

            } else {
                //2.2该商家购物车中没有有该商品
                //新增明细
                orderItem = createOrderItem(item, num);
                orderItemList.add(orderItem);
            }



        } else { //3.购物车列表中没有该商家购物车
            //3.1新建购物车
            cart = new Cart();
            cart.setSellerId(item.getSellerId());
            cart.setSellerName(item.getSeller());
            //新建商品明细列表
            List<TbOrderItem> orderItemList = new ArrayList<>();
            //新建商品明细tbOrderItem
            TbOrderItem orderItem = createOrderItem(item, num);
            orderItemList.add(orderItem);
            cart.setOrderItemList(orderItemList);
            cartList.add(cart);
        }


        return cartList;
    }

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public List<Cart> findCartListFromRedis(String sessionId) {
       String carStr= (String) redisTemplate.boundValueOps(sessionId).get();
       if (carStr==null){
           carStr="[]";
       }
       List<Cart> cartList= JSON.parseArray(carStr,Cart.class);
        return cartList;
    }

    @Override
    public void addCartListToRedis(String sessionId, List<Cart> cartList) {
        String carStr = JSON.toJSONString(cartList);
        redisTemplate.boundValueOps(sessionId).set(carStr,7L, TimeUnit.DAYS);
    }

    @Override
    public void addCartListToRedisByUserName(String username, List<Cart> cartList) {
        String carStr = JSON.toJSONString(cartList);
        redisTemplate.boundValueOps(username).set(carStr,7L, TimeUnit.DAYS);
    }

    @Override
    public List<Cart> mergeCartList(List<Cart> cartList_sessionId, List<Cart> cartList_username) {
        for (Cart cart : cartList_sessionId) {
            List<TbOrderItem> orderItemList = cart.getOrderItemList();
            for (TbOrderItem orderItem : orderItemList) {
                addItemToCartList(cartList_username,orderItem.getItemId(),orderItem.getNum());
            }
        }
        return cartList_username;
    }

    @Override
    public void deleteCartList(String sessionId) {
        redisTemplate.delete(sessionId);
    }

    //判断orderItemList中是否有item
    private TbOrderItem searchOrderItemFromCart(List<TbOrderItem> orderItemList, TbItem item) {
        for (TbOrderItem orderItem : orderItemList) {
            if (orderItem.getItemId().equals(item.getId())) {
                return orderItem;
            }
        }
        return null;
    }


    //根据item创建orderItem
    private TbOrderItem createOrderItem(TbItem item, Integer num) {
        if (num < 0) {
            throw new RuntimeException("商品数量不能小于0");
        }
        TbOrderItem orderItem = new TbOrderItem();
        orderItem.setItemId(item.getId());
        orderItem.setGoodsId(item.getGoodsId());
        orderItem.setTitle(item.getTitle());
        orderItem.setPrice(item.getPrice());
        orderItem.setNum(num);
        orderItem.setTotalFee(new BigDecimal(item.getPrice().doubleValue() * num));
        orderItem.setPicPath(item.getImage());
        orderItem.setSellerId(item.getSellerId());
        return orderItem;
    }

    //判断购物车列表中是否有该商家的购物车
    private Cart searchCartFromCartList(List<Cart> cartList, String sellerId) {
        for (Cart cart : cartList) {
            if (cart.getSellerId().equals(sellerId)) {
                return cart;
            }
        }
        return null;
    }
}
