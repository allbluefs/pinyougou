package com.pinyougou.shop.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.pojo.TbBrandExample;
import com.pinyougou.sellergoods.service.BrandService;
import entity.PageResult;
import entity.Result;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @Date:2019/7/1
 */
@RestController
@RequestMapping("/brand")
public class BrandController {
    @Reference
    private BrandService brandService;

    @RequestMapping("/list")
    public List<TbBrand> list(){
        List<TbBrand> tbBrandList = brandService.findAll(null);
        return tbBrandList;
    }

    @RequestMapping("/findPage")
    public PageResult findPage(int pageNumber,int pageSize){
        return brandService.findPage(pageNumber,pageSize);
    }
    @RequestMapping("/findPageCondition")
    public PageResult findPageCondition(int pageNumber,int pageSize,@RequestBody TbBrand tbBrand){
        TbBrandExample example=new TbBrandExample();
        if (!StringUtils.isEmpty(tbBrand.getName())) {
            example.createCriteria().andNameEqualTo(tbBrand.getName());
        }
        if (!StringUtils.isEmpty(tbBrand.getFirstChar())) {
            example.createCriteria().andFirstCharEqualTo(tbBrand.getFirstChar());
        }
        if (StringUtils.isEmpty(tbBrand.getName()) &&StringUtils.isEmpty(tbBrand.getFirstChar()) ) {
            return brandService.findPage(pageNumber,pageSize);
        }
        PageResult pageResult = brandService.findAll(example, pageNumber, pageSize);
        return pageResult;
    }

    @RequestMapping("/add")
    public Result add(@RequestBody TbBrand tbBrand){
        try {
            brandService.save(tbBrand);
            return new Result(true,"");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"保存失败");
        }
    }
    @RequestMapping("/findOne")
    public TbBrand findOne(Long id){
        return brandService.findById(id);
    }

    @RequestMapping("/update")
    public Result update(@RequestBody TbBrand tbBrand){
        try {
            brandService.update(tbBrand);
            return new Result(true,"");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"更新失败");
        }
    }

    @RequestMapping("/delete")
    public Result delete(Long[] ids){
        try {
            for (Long id : ids) {
                brandService.delete(id);
            }
            return new Result(true,"");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"删除失败");
        }
    }

    @RequestMapping("/selectBrandOptions")
    public List<Map> selectBrandOptions(){
        List<Map> maps = brandService.selectBrandOptions();
        return maps;
    }

}
