package com.lin.missyou.repository;

import com.lin.missyou.model.Banner;
import com.lin.missyou.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * JPA有8种查询
 * JPA的查询需要extends JpaRepository<Banner, Long> 后面那个是主键的类型
 */
public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
    Optional<User> findByOpenid(String openid);
    User findFirstById(Long id);
    User findByUnifyUid(Long uuid);
}
