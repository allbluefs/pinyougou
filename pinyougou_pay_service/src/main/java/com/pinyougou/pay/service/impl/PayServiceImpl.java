package com.pinyougou.pay.service.impl;


import com.alibaba.dubbo.config.annotation.Service;
import com.github.wxpay.sdk.WXPayUtil;
import com.pinyougou.mapper.TbOrderMapper;
import com.pinyougou.mapper.TbPayLogMapper;
import com.pinyougou.pay.service.PayService;
import com.pinyougou.pojo.TbOrder;
import com.pinyougou.pojo.TbOrderExample;
import com.pinyougou.pojo.TbPayLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import utils.HttpClient;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Date:2019/7/26
 */
@Service
@Transactional
public class PayServiceImpl implements PayService {

    @Value("${appid}")
    private String appid;
    @Value("${partner}")
    private String partner;
    @Value("${partnerkey}")
    private String partnerkey;
    @Value("${notifyurl}")
    private String notifyurl;


    @Override
    public Map<String, Object> createNative(String out_trade_no, String total_fee) throws Exception {
        //封装请求参数
        Map<String ,String> paramMap=new HashMap<>();
        paramMap.put("appid",appid);
        paramMap.put("mch_id",partner);
        paramMap.put("nonce_str", WXPayUtil.generateNonceStr());
        paramMap.put("body","品优购");
        paramMap.put("out_trade_no",out_trade_no);
        paramMap.put("total_fee",total_fee);
        paramMap.put("spbill_create_ip","127.0.0.1");
        paramMap.put("notify_url",notifyurl);
        paramMap.put("trade_type","NATIVE" );
        paramMap.put("product_id","1" );
        //转换参数
        String paramXml = WXPayUtil.generateSignedXml(paramMap, partnerkey);
        System.out.println(paramXml);
        //基于HttpClient向微信发送请求
        HttpClient httpClient=new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
        httpClient.setHttps(true);
        httpClient.setXmlParam(paramXml);
        httpClient.post();

        //处理结果
        String content = httpClient.getContent();
        Map<String, String> resultMap = WXPayUtil.xmlToMap(content);

        //将需要的结果重新封装到map中
        Map<String,Object> map=new HashMap<>();
        map.put("code_url",resultMap.get("code_url"));
        map.put("out_trade_no",out_trade_no);
        map.put("total_fee",total_fee);

        return map;
    }

    @Override
    public Map<String, String> queryPayStatus(String out_trade_no) throws Exception {
        Map<String ,String> paramMap=new HashMap<>();
        paramMap.put("appid",appid);
        paramMap.put("mch_id",partner);
        paramMap.put("nonce_str", WXPayUtil.generateNonceStr());
        paramMap.put("out_trade_no",out_trade_no);
        //转换参数
        String paramXml = WXPayUtil.generateSignedXml(paramMap, partnerkey);
        System.out.println(paramXml);
        //基于HttpClient向微信发送请求
        HttpClient httpClient=new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");
        httpClient.setHttps(true);
        httpClient.setXmlParam(paramXml);
        httpClient.post();
        //处理结果
        String content = httpClient.getContent();
        Map<String, String> resultMap = WXPayUtil.xmlToMap(content);

        return resultMap;
    }

    @Autowired
    private RedisTemplate redisTemplate;
    @Override
    public TbPayLog getPayLogFromRedis(String userId) {
        TbPayLog payLog= (TbPayLog) redisTemplate.boundHashOps("payLog").get(userId);
        return payLog;
    }

    @Autowired
    private TbPayLogMapper payLogMapper;
    @Autowired
    private TbOrderMapper orderMapper;
    @Override
    public void updatePayStatus(String out_trade_no, String transaction_id) {
        TbPayLog payLog = payLogMapper.selectByPrimaryKey(out_trade_no);
        String orders = payLog.getOrderList();
        String[] orderList = orders.split(",");
        payLog.setTradeState("2");
        payLog.setPayTime(new Date());
        payLog.setTransactionId(transaction_id);
        payLogMapper.updateByPrimaryKey(payLog);

        for (String orderId : orderList) {
            TbOrder tbOrder = orderMapper.selectByPrimaryKey(Long.parseLong(orderId));
            tbOrder.setStatus("2");
            tbOrder.setPaymentTime(new Date());
            orderMapper.updateByPrimaryKey(tbOrder);
        }
        //清空缓存中的支付日志
        redisTemplate.boundHashOps("payLog").delete(payLog.getUserId());
    }
}
