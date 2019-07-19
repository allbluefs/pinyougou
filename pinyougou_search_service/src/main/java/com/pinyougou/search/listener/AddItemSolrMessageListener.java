package com.pinyougou.search.listener;

import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.List;

/**
 * @Date:2019/7/18
 */
public class AddItemSolrMessageListener implements MessageListener {

    @Autowired
    private SolrTemplate solrTemplate;
    @Autowired
    private TbItemMapper itemMapper;
    @Override
    public void onMessage(Message message) {
        try {
            TextMessage textMessage= (TextMessage) message;
            String goodsId = textMessage.getText();
            TbItemExample example=new TbItemExample();
            example.createCriteria().andGoodsIdEqualTo(Long.parseLong(goodsId));
            List<TbItem> itemList = itemMapper.selectByExample(example);

            solrTemplate.saveBeans(itemList);
            solrTemplate.commit();
        } catch (JMSException e) {
            e.printStackTrace();
        }

    }
}
