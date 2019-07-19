package com.pinyougou.sellergoods.service;

import com.github.pagehelper.PageInfo;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.pojo.TbBrandExample;
import entity.PageResult;

import java.util.List;
import java.util.Map;

public interface BrandService {

    PageResult findAll(TbBrandExample example, int page, int size);

    List<TbBrand> findAll(TbBrandExample example);

    TbBrand findById(Long id);

    void save(TbBrand tbBrand);

    void update(TbBrand tbBrand);

    void delete(Long id);

    PageResult findPage(int pageNumber, int pageSize);

    List<Map> selectBrandOptions();

}
