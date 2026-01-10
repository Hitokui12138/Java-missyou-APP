package com.lin.missyou.vo;

import com.lin.missyou.model.Category;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;


/**
 * Category Model里面有Coupon的双向多对多关系
 * 为了防止循环序列化, 创建这个VO来精简Category的属性
 */
@Getter
@Setter
public class CategoryPureVO {

    private Long id;

    private String name;

    //private String description;

    private Boolean isRoot;

    private String img;

    private Long parentId;

    private Long index;

    // Coupon相关的也去掉

    public CategoryPureVO(Category category){
        BeanUtils.copyProperties(category, this);
    }
}
