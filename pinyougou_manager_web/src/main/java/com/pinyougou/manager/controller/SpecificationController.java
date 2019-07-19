package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbSpecification;
import com.pinyougou.sellergoods.service.SpecificationService;
import entity.PageResult;
import entity.Result;
import groupEntity.Specification;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @Date:2019/7/4
 */
@RestController
@RequestMapping("/specification")
public class SpecificationController {
    @Reference
    private SpecificationService specificationService;

    @RequestMapping("/findPage")
    public PageResult findPage(Integer pageNumber, Integer pageSize, @RequestBody TbSpecification tbSpecification){
       return specificationService.search(tbSpecification, pageNumber, pageSize);
    }

    @RequestMapping("/add")
    public Result add(@RequestBody Specification specification){
        try {
            System.out.println(1);
            specificationService.save(specification);
            return new Result(true,"");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"添加规格失败");
        }
    }
    @RequestMapping("/findOne")
    public Specification findOne(Long id){
        return specificationService.findOne(id);
    }

    @RequestMapping("/update")
    public Result update(@RequestBody Specification specification){
        try {
            specificationService.update(specification);
            return new Result(true,"");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"更新规格失败");
        }
    }
    @RequestMapping("/dele")
    public Result dele(Long [] ids){
        try {
            for (Long id : ids) {
                specificationService.delete(id);
            }
            return new Result(true,"");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"删除规格失败");
        }
    }
    @RequestMapping("/selectSpecOptions")
    public List<Map> selectSpecOptions(){
        return specificationService.selectSpecOptions();
    }


}
