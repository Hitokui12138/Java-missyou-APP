package com.lin.missyou.service;

import com.lin.missyou.model.Category;
import com.lin.missyou.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    /**
     * 如何返回两个结果?
     * 组合成一个结果
     * 1. HashMap
     * 2. 定义Pair<T, K>
     */
    public Map<Integer, List<Category>> getAll(){
        //根节点
        List<Category> roots = categoryRepository.findAllByIsRootOrderByIndexAsc(true);
        //二级节点
        List<Category> subs = categoryRepository.findAllByIsRootOrderByIndexAsc(false);

        Map<Integer, List<Category>> categories = new HashMap<>();
        categories.put(1, roots);
        categories.put(2, subs);

        return categories;
    }
}
