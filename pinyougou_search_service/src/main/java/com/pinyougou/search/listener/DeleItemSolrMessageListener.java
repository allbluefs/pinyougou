package com.pinyougou.search.listener;

import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

/**
 * @Date:2019/7/18
 */
public class DeleItemSolrMessageListener implements MessageListener {

    @Autowired
    private SolrTemplate solrTemplate;
    @Override
    public void onMessage(Message message) {
        try {
            TextMessage textMessage= (TextMessage) message;
            String goodsId = textMessage.getText();

            Query query=new SimpleQuery("item_goodsid:"+goodsId);
            solrTemplate.delete(query);
            solrTemplate.commit();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
