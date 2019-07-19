package com.pinyougou.manager.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @Date:2019/7/6
 */
@RestController
@RequestMapping("/login")
public class LoginController {

    @RequestMapping("/getLoginName")
    public Map<String,String> getLoginName(){
        //基于安全框架springsecurity获取登录人用户名
        String loginName = SecurityContextHolder.getContext().getAuthentication().getName();
        Map<String,String> map=new HashMap<>();
        map.put("loginName",loginName);
        return map;
    }
}
