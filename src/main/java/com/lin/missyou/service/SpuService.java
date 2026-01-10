package com.lin.missyou.service;

import com.lin.missyou.model.Banner;
import com.lin.missyou.model.Spu;
import com.lin.missyou.repository.SpuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * service层用来编写业务逻辑
 */
@Service //应该打在实现类上
public class SpuService {
    //注入BannerRepository DAO
    @Autowired
    private SpuRepository spuRepository;

    /**
     * 前端页面除了SPU,还需要其他优惠券等等数据
     * @param id
     * @return
     */
    public Spu getSpu(Long id) {
        return spuRepository.findOneById(id);
    }

    /**
     * 还需要排序,分页, 数据冗余的问题
     * 注意这里使用findAll(Page page)的重载方法
     * 返回的不是List而是Page
     * @return
     */
    public Page<Spu> getLatestPagingSpu(Integer pageNum, Integer size){
        // 默认分页方法是page和size,这里转化成start和count
        Pageable pageable = PageRequest.of(
                pageNum,
                size,
                Sort.by("createTime") //根据Model定义的名称
                        .descending()); //倒叙
        return this.spuRepository.findAll(pageable);// 默认方法也可以不在Repository中定义
    }

    /**
     * JPA 带上WHERE条件进行查询
     */
    public Page<Spu> getByCategory(Long cid, Boolean isRoot, Integer pageNum, Integer size){
        Pageable page = PageRequest.of(pageNum, size);
        if(isRoot){
            return this.spuRepository.findByRootCategoryIdOrderByCreateTime(cid, page);
        }else{
            return this.spuRepository.findByCategoryIdOrderByCreateTimeDesc(cid, page);
        }
    }
}
