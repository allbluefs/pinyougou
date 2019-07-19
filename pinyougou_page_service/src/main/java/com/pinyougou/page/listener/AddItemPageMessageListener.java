package com.pinyougou.page.listener;

import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.page.service.PageService;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;
import freemarker.template.Configuration;
import freemarker.template.Template;
import groupEntity.Goods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Date:2019/7/18
 */
public class AddItemPageMessageListener implements MessageListener {
    @Autowired
    private FreeMarkerConfig freeMarkerConfig;
    @Autowired
    private PageService pageService;
    @Override
    public void onMessage(Message message) {
        try {
            //1.创建配置类
            Configuration configuration=freeMarkerConfig.getConfiguration();
            //2.加载模板
            Template template=configuration.getTemplate("item.ftl");
            //3.创建数据模型
            TextMessage textMessage= (TextMessage) message;
            String goodsId = textMessage.getText();
            Goods goods = pageService.findOne(Long.parseLong(goodsId));
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

        } catch (Exception e) {
            e.printStackTrace();

        }
    }
}
