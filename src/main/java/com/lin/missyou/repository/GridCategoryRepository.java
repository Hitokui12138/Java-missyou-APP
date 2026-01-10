package com.lin.missyou.repository;

import com.lin.missyou.model.GridCategory;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 没有筛选需求, 留空即可
 */
public interface GridCategoryRepository extends JpaRepository<GridCategory, Long> {

}
