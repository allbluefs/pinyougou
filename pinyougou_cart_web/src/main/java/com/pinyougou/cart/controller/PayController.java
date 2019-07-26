package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pay.service.PayService;
import com.pinyougou.pojo.TbPayLog;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import utils.IdWorker;

import java.util.HashMap;
import java.util.Map;

/**
 * @Date:2019/7/26
 */
@RestController
@RequestMapping("/pay")
public class PayController {
    @Reference
    private PayService payService;

    @RequestMapping("/createNative")
    public Map<String,Object> createNative(){

        try {
            //获取支付用户
            String userId = SecurityContextHolder.getContext().getAuthentication().getName();
            TbPayLog payLog=payService.getPayLogFromRedis(userId);
            IdWorker idWorker=new IdWorker();
            return payService.createNative(payLog.getOutTradeNo(),payLog.getTotalFee()+"");
        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }
    @RequestMapping("/queryPayStatus")
    public Result queryPayStatus(String out_trade_no){
        try {
            int count=1;
            while (true){
                Thread.sleep(3000);
                //五分钟未支付,跳出循环
                count++;
                if (count>100){
                    return new Result(false,"timeout");
                }
                Map<String, String> resultMap = payService.queryPayStatus(out_trade_no);
                String trade_state = resultMap.get("trade_state");
                if ("SUCCESS".equals(trade_state)) {
                    String transaction_id = resultMap.get("transaction_id");
                    payService.updatePayStatus(out_trade_no,transaction_id);
                    return new Result(true,"支付成功");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"支付失败");
        }
    }

}
