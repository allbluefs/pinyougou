package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.pojo.TbBrandExample;
import com.pinyougou.sellergoods.service.BrandService;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * @Date:2019/7/1
 */
@Service
public class BrandServiceImpl implements BrandService {
    @Autowired
    private TbBrandMapper tbBrandMapper;

    @Override
    public PageResult findAll(TbBrandExample example, int page, int size) {
        PageHelper.startPage(page,size);
        Page<TbBrand> list = (Page<TbBrand>) tbBrandMapper.selectByExample(example);
        PageResult pageResult = new PageResult(list.getTotal(), list.getResult());
        return pageResult;
    }

    @Override
    public List<TbBrand> findAll(TbBrandExample example) {
        return tbBrandMapper.selectByExample(example);
    }

    @Override
    public TbBrand findById(Long id) {
        return tbBrandMapper.selectByPrimaryKey(id);
    }

    @Override
    public void save(TbBrand tbBrand) {
        tbBrandMapper.insert(tbBrand);
    }

    @Override
    public void update(TbBrand tbBrand) {
        tbBrandMapper.updateByPrimaryKeySelective(tbBrand);
    }

    @Override
    public void delete(Long id) {
        tbBrandMapper.deleteByPrimaryKey(id);
    }

    @Override
    public PageResult findPage(int pageNumber, int pageSize) {
        PageHelper.startPage(pageNumber,pageSize);
        Page<TbBrand> list = (Page<TbBrand>) tbBrandMapper.selectByExample(null);
        return new PageResult(list.getTotal(),list.getResult());
    }

    @Override
    public List<Map> selectBrandOptions() {
        return tbBrandMapper.selectBrandOptions();
    }
}
