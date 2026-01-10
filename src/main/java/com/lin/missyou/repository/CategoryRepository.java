package com.lin.missyou.repository;

import com.lin.missyou.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    /**
     * 可以用一个Boolean参数来控制isRoot或者不是Root
     * @param isRoot
     * @return
     */
    List<Category> findAllByIsRootOrderByIndexAsc(Boolean isRoot);
}
