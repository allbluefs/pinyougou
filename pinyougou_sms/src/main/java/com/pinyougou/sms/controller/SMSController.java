package com.pinyougou.sms.controller;

import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.pinyougou.sms.utils.SmsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @Date:2019/7/19
 */
@RestController
@RequestMapping("/sms")
public class SMSController {
    @Autowired
    private SmsUtil smsUtil;


    /**
     * 接口：http://localhost:10086/sms/sendSms.do
     *
     */
    @RequestMapping(value = "/sendSms",method = RequestMethod.POST)
    public Map<String,String> sendSms(String phoneNumbers,String signName,String templateCode,String param){
        try {
            //调用封装的阿里云工具类发短信
            SendSmsResponse response = smsUtil.sendSms(phoneNumbers, signName, templateCode, param);
            System.out.println("短信接口返回的数据----------------");
            System.out.println("Code=" + response.getCode());
            System.out.println("Message=" + response.getMessage());
            System.out.println("RequestId=" + response.getRequestId());
            System.out.println("BizId=" + response.getBizId());
            Map<String,String> result=new HashMap<>();
            result.put("Code",response.getCode());
            result.put("Message",response.getMessage());
            result.put("RequestId",response.getRequestId());
            result.put("BizId",response.getBizId());
            return result;
        } catch (ClientException e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }
}
