package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.SearchService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.HighlightEntry;
import org.springframework.data.solr.core.query.result.HighlightPage;
import org.springframework.data.solr.core.query.result.ScoredPage;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Date:2019/7/15
 */
@Service
@Transactional
public class SearchServiceImpl implements SearchService {

    @Autowired
    private SolrTemplate solrTemplate;

    @Override
    public Map<String, Object> searchAll(Map<String, Object> searchMap) {
        HighlightQuery query = new SimpleHighlightQuery();
        String keywords = (String) searchMap.get("keywords");
        Criteria criteria=null;
        //1、关键字搜索
        //获取页面提交的搜索关键字
        if (!StringUtils.isBlank(keywords)) {
            criteria=new Criteria("item_keywords").is(keywords);
        }else {
            criteria=new Criteria().expression("*:*");
        }
        query.addCriteria(criteria);

        //过滤条件查询
        //2、获取分类条件
        String category = (String) searchMap.get("category");
        if (StringUtils.isNotBlank(category)) {
            Criteria categoryCriteria=new Criteria("item_category").is(category);
            FilterQuery filterQuery=new SimpleFilterQuery(categoryCriteria);
            query.addFilterQuery(filterQuery);
        }

        //3.获取品牌条件
        String brand = (String) searchMap.get("brand");
        if (StringUtils.isNotBlank(brand)) {
            Criteria brandCriteria=new Criteria("item_brand").is(brand);
            FilterQuery filterQuery=new SimpleFilterQuery(brandCriteria);
            query.addFilterQuery(filterQuery);
        }

        //4.获取规格分类条件
        Map<String,String> specMap = (Map<String, String>) searchMap.get("spec");
        for (String key : specMap.keySet()) {
            Criteria specCriteria=new Criteria("item_spec_"+key).is(specMap.get(key));
            FilterQuery filterQuery=new SimpleFilterQuery(specCriteria);
            query.addFilterQuery(filterQuery);
        }

        //5.获取价格分类条件
        String price = (String) searchMap.get("price");
        String[] split = price.split("-");
        if (StringUtils.isNotBlank(price)) {
            if (!"0".equals(split[0])) {
                Criteria criteria1=new Criteria("item_price").greaterThanEqual(split[0]);
                FilterQuery filterQuery=new SimpleFilterQuery(criteria1);
                query.addFilterQuery(filterQuery);
            }
            if (!"*".equals(split[1])) {
                Criteria criteria1=new Criteria("item_price").lessThanEqual(split[1]);
                FilterQuery filterQuery=new SimpleFilterQuery(criteria1);
                query.addFilterQuery(filterQuery);
            }
        }

        //6.排序查询
        String sortField = (String) searchMap.get("sortField");
        String sort = (String) searchMap.get("sort");

        if (StringUtils.isNotBlank(sortField)) {
            if ("ASC".equals(sort)) {
                query.addSort(new Sort(Sort.Direction.ASC,"item_"+sortField));
            }else {
                query.addSort(new Sort(Sort.Direction.DESC,"item_"+sortField));
            }
        }
        //7.分页查询
        Integer pageNo = (Integer) searchMap.get("pageNo");
        int rows=60;
        //7、分页条件查询
        query.setOffset((pageNo-1)*rows);//分页索引起始值 默认：0  （pageNo-1)*rows
        query.setRows(rows);//每页展示记录数

        //高亮处理
        //创建高亮对象
        HighlightOptions highlightOptions=new HighlightOptions();
        //设置高亮字段
        highlightOptions.addField("item_title");
        //设置高亮前缀和后缀
        highlightOptions.setSimplePrefix("<font color='red'>");
        highlightOptions.setSimplePostfix("</font>");
        query.setHighlightOptions(highlightOptions);

        HighlightPage<TbItem> page = solrTemplate.queryForHighlightPage(query, TbItem.class);
        //获取当前页列表
        List<TbItem> itemList=page.getContent();
        //处理高亮结果
        for (TbItem item : itemList) {
            //获取高亮结果集
            List<HighlightEntry.Highlight> highlights = page.getHighlights(item);
            if (highlights != null && highlights.size() > 0) {
                //获取高亮对象
                HighlightEntry.Highlight highlight = highlights.get(0);
                //获取高亮内容
                List<String> snipplets = highlight.getSnipplets();
                if (snipplets != null && snipplets.size() > 0) {
                    String title = snipplets.get(0);
                    item.setTitle(title);
                }
            }

        }

        Map<String,Object> resultMap=new HashMap<>();
        resultMap.put("rows",itemList);
        resultMap.put("pageNo",pageNo);//当前页
        resultMap.put("totalPages",page.getTotalPages());//总页数
        return resultMap;
    }
}
