package com.lin.missyou.repository;

import com.lin.missyou.model.Sku;
import com.lin.missyou.model.Spu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SkuRepository extends JpaRepository<Sku, Long> {
    List<Sku> findAllByIdIn(List<Long> Ids);

    @Modifying// @Query只能查询, 更新的话需要这个注解
    @Query("update Sku s \n" +
            "set s.stock = s.stock - :quantity\n" +
            "where s.id = :sid\n" +
            "and s.stock >= :quantity")
    int reduceStock(Long sid, Long quantity);

    @Modifying
    @Query("update Sku s\n" +
            "set s.stock = s.stock + (:quantity)\n" +
            "where s.id = :sid")
    int recoverStock(
            @Param("sid") Long sid,
            @Param("quantity") Long quantity
    );
}
