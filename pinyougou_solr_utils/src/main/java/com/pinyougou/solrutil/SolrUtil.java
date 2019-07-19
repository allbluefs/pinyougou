package com.pinyougou.solrutil;

import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @Date:2019/7/14
 */
@Component
public class SolrUtil {
    @Autowired
    private TbItemMapper itemMapper;
    @Autowired
    private SolrTemplate solrTemplate;

    public void dataImport() {
        List<TbItem> itemList=itemMapper.findAllGrounding();

        for (TbItem item : itemList) {
            Map<String,String> specMap = JSON.parseObject(item.getSpec(), Map.class);
            item.setSpecMap(specMap);
        }
        solrTemplate.saveBeans(itemList);
        solrTemplate.commit();
    }
}
