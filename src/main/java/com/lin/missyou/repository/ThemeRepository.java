package com.lin.missyou.repository;

import com.lin.missyou.model.Theme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * 这里讲一下用SQL的写法
 * 1. 原生SQL 操作数据表
 *  1.1 如果想写原生SQL, 需要把@Query()的参数设置一下, 默认是写JPQL
 * 2. JPQL Java Persistence Query Language
 *  2.2 优势在于可以操作Model对象的
 *  2.3 但有些SQL的关键字不支持
 */
public interface ThemeRepository extends JpaRepository<Theme, Long> {

    /**
     * 这条语句中 Theme是Model的名字,数据库的表名是小写的theme
     * 1. 用 @Param("names") 指定参数名, 之后就可以代入JPQL了, 代入方式是固定的(:XXXX)
     *  1.1 (:XXXX)表示
     * 2. 如果和参数同名的话, @Param也可以省略
     * @param names
     * @return
     */
    @Query("select t from Theme t where t.name in (:names)")
    List<Theme> findByNames(@Param("names") List<String> names);

    /**
     * 命名查询
     * @param name
     * @return
     */
    Optional<Theme> findByName(String name);
}
