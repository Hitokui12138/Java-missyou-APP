package com.lin.missyou.repository;

import com.lin.missyou.model.Banner;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * JPA有8种查询
 * JPA的查询需要extends JpaRepository<Banner, Long> 后面那个是主键的类型
 */
public interface BannerRepository extends JpaRepository<Banner, Long> {
    Banner findOneById(Long i);
    Banner findOneByName(String name);
}
