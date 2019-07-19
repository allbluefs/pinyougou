package com.pinyougou.sellergoods.service;

import com.pinyougou.pojo.TbSpecification;
import com.pinyougou.pojo.TbSpecificationExample;
import entity.PageResult;
import groupEntity.Specification;

import java.util.List;
import java.util.Map;

public interface SpecificationService {
    PageResult search( TbSpecification tbSpecification, int page, int size);

    List<TbSpecification> findAll(TbSpecificationExample example);

    TbSpecification findById(Long id);

    void save(Specification specification);

    void update(Specification specification);

    void delete(Long id);

    PageResult findPage(int pageNumber, int pageSize);

    Specification findOne(Long id);

    List<Map> selectSpecOptions();

}
