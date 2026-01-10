package com.lin.missyou.repository;

import com.lin.missyou.model.Banner;
import com.lin.missyou.model.Spu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * JPA有8种查询
 * JPA的查询需要extends JpaRepository<Banner, Long>
 * 前面是试题类型,后面那个是主键的类型
 */
public interface SpuRepository extends JpaRepository<Spu, Long> {
    //1. 单表查询
    @Query("")
    Spu findOneById(Long id);

    //2. findAll(page) 之类的基础方法不用在接口里写出来
    //由于参数里有pageable, 因此返回结果也是分页的

    //3,4. 带条件的findAll()
    //只要命名规范,JPA就能推断出“SELECT * FROM spu WHRER category_id = cid”
    Page<Spu> findByCategoryIdOrderByCreateTimeDesc(Long cid, Pageable pageable);
    Page<Spu> findByRootCategoryIdOrderByCreateTime(Long cid, Pageable pageable);


}
