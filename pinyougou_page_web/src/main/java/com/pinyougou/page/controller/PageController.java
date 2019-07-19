package com.pinyougou.page.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.page.service.PageService;
import com.pinyougou.pojo.TbItem;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import groupEntity.Goods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

/**
 * @Date:2019/7/17
 */
@RestController
@RequestMapping("/page")
public class PageController {
    @Reference
    private PageService pageService;

    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;
    @RequestMapping("/genHtml")
    public String genHtml(Long goodsId){
        try {
            //1.创建配置类
            Configuration configuration=freeMarkerConfigurer.getConfiguration();
            //2.加载模板
            Template template=configuration.getTemplate("item.ftl");
            //3.创建数据模型
            Goods goods = pageService.findOne(goodsId);
            List<TbItem> itemList = goods.getItemList();
            for (TbItem item : itemList) {
                Map<String,Object> map=new HashMap<>();
                map.put("goods",goods);
                map.put("item",item);
                //4.创建一个 Writer 对象，一般创建一 FileWriter 对象，指定生成的文件名。
                Writer writer=new FileWriter("D:\\workspace2\\item\\"+item.getId()+".html");
                //7.调用模板对象的process方法输出文件
                template.process(map,writer);
                //8.关闭流
                writer.close();
            }
            return "success....";
        } catch (Exception e) {
            e.printStackTrace();
            return "failed....";
        }
    }
}
