package com.pinyougou.cart.service;

import groupEntity.Cart;

import java.util.List;

public interface CartService {
    // 添加商品到购物车列表(1.要加商品的列表  2.加入商品的id  3.加入商品数量)
    List<Cart> addItemToCartList(List<Cart> cartList,Long itemId,Integer num);
    //基于sessionId从redis中获取购物车列表
    List<Cart> findCartListFromRedis(String sessionId);
    //基于sessionId将购物车列表存入redis
    void addCartListToRedis(String sessionId,List<Cart> cartList);

    void addCartListToRedisByUserName(String username,List<Cart> cartList);
}
