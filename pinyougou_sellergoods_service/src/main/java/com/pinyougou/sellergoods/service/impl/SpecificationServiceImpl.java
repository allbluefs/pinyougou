package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbSpecificationMapper;
import com.pinyougou.mapper.TbSpecificationOptionMapper;
import com.pinyougou.pojo.TbSpecification;
import com.pinyougou.pojo.TbSpecificationExample;
import com.pinyougou.pojo.TbSpecificationOption;
import com.pinyougou.pojo.TbSpecificationOptionExample;
import com.pinyougou.sellergoods.service.SpecificationService;
import entity.PageResult;
import groupEntity.Specification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * @Date:2019/7/4
 */
@Service
@Transactional
public class SpecificationServiceImpl implements SpecificationService {

    @Autowired
    private TbSpecificationMapper tbSpecificationMapper;

    @Autowired
    private TbSpecificationOptionMapper tbSpecificationOptionMapper;


    @Override
    public PageResult search(TbSpecification tbSpecification, int page, int size) {
        PageHelper.startPage(page,size);
        TbSpecificationExample example=new TbSpecificationExample();
        if (!StringUtils.isEmpty(tbSpecification.getSpecName())) {
            example.createCriteria().andSpecNameLike("%"+tbSpecification.getSpecName()+"%");
        }
        else {
            example=null;
        }
        Page<TbSpecification> list = (Page<TbSpecification>) tbSpecificationMapper.selectByExample(example);
        return new PageResult(list.getTotal(),list.getResult());
    }

    @Override
    public List<TbSpecification> findAll(TbSpecificationExample example) {
        return null;
    }

    @Override
    public TbSpecification findById(Long id) {
        return tbSpecificationMapper.selectByPrimaryKey(id);
    }

    @Override
    public void save(Specification specification) {
        TbSpecification tbSpecification = specification.getSpecification();
        tbSpecificationMapper.insert(tbSpecification);
        List<TbSpecificationOption> specificationOptions = specification.getSpecificationOptions();
        for (TbSpecificationOption specificationOption : specificationOptions) {
            specificationOption.setSpecId(tbSpecification.getId());
            tbSpecificationOptionMapper.insert(specificationOption);
        }
    }

    @Override
    public void update(Specification specification) {
        TbSpecification tbSpecification = specification.getSpecification();
        tbSpecificationMapper.updateByPrimaryKey(tbSpecification);

        TbSpecificationOptionExample example=new TbSpecificationOptionExample();
        example.createCriteria().andSpecIdEqualTo(tbSpecification.getId());
        List<TbSpecificationOption> tbSpecificationOptions = tbSpecificationOptionMapper.selectByExample(example);
        for (TbSpecificationOption tbSpecificationOption : tbSpecificationOptions) {
            tbSpecificationOptionMapper.deleteByPrimaryKey(tbSpecificationOption.getId());
        }

        List<TbSpecificationOption> specificationOptions = specification.getSpecificationOptions();
        for (TbSpecificationOption specificationOption : specificationOptions) {
            specificationOption.setSpecId(tbSpecification.getId());
            tbSpecificationOptionMapper.insert(specificationOption);
        }

    }

    @Override
    public void delete(Long id) {
        tbSpecificationMapper.deleteByPrimaryKey(id);
        TbSpecificationOptionExample example=new TbSpecificationOptionExample();
        example.createCriteria().andSpecIdEqualTo(id);
        List<TbSpecificationOption> tbSpecificationOptions = tbSpecificationOptionMapper.selectByExample(example);
        for (TbSpecificationOption tbSpecificationOption : tbSpecificationOptions) {
            tbSpecificationOptionMapper.deleteByPrimaryKey(tbSpecificationOption.getId());
        }
    }

    @Override
    public PageResult findPage(int pageNumber, int pageSize) {
        return null;
    }

    @Override
    public Specification findOne(Long id) {
        Specification specification=new Specification();
        TbSpecification tbSpecification = tbSpecificationMapper.selectByPrimaryKey(id);
        specification.setSpecification(tbSpecification);
        TbSpecificationOptionExample example=new TbSpecificationOptionExample();
        example.createCriteria().andSpecIdEqualTo(id);
        List<TbSpecificationOption> tbSpecificationOptions = tbSpecificationOptionMapper.selectByExample(example);
        specification.setSpecificationOptions(tbSpecificationOptions);
        return specification;
    }

    @Override
    public List<Map> selectSpecOptions() {
        return tbSpecificationMapper.selectSpecOptions();
    }
}
