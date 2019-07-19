package com.pinyougou.search.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.search.service.SearchService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @Date:2019/7/15
 */
@RestController
@RequestMapping("/search")
public class SearchController {
    @Reference
    private SearchService searchService;

    @RequestMapping("/searchAll")
    public Map<String,Object> searchAll(@RequestBody Map<String,Object> searchMap){
        return searchService.searchAll(searchMap);
    }
}
