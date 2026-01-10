package com.lin.missyou.vo;

import com.lin.missyou.model.Category;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * VO的意义:
 * 前端想要的数据结构和数据库的数据结构不同时
 * 定义前端需求于VO, 然后把数据库的值Mapping进去
 * 然后向前端返回VO
 *
 * 一些不想写在Controller里的数据转换逻辑也可以写在
 */
@Getter
@Setter
public class CategoriesAllVO {
    private List<CategoryPureVO> roots;
    private List<CategoryPureVO> subs;

    /**
     * Category和Coupon有多对多的关系,这回造成两个问题
     * 1. 前端在获取页面信息时不需要Coupon
     * 2. 双向多对多关系可能造成循环序列化,从而内存泄露
     *      2.1 配置双向关系后,要有选择地序列化,只序列化一遍 @JsonIgnore
     *      2.2 创建Category的VO来去掉不要的导航属性和字段
     */
    public CategoriesAllVO(Map<Integer, List<Category>> map){
//        List<Category> roots = map.get(1);
//        roots.foreach(r ->{ //Consumer
//            CategoryPureVO vo = new CategoryPureVO(r);
//            this.roots.add(vo);
//                }
//        )

        // 用Stream解决这个
        this.roots = map.get(1).stream()
                .map(r -> { //把每一个Category map成 VO
                    return new CategoryPureVO(r);
                })
                .collect(Collectors.toList()); //最终结果要List, 因此collect
        //把每一个Category map成 VO
        this.subs = map.get(2).stream()
                .map(CategoryPureVO::new)
                .collect(Collectors.toList()); //最终结果要List, 因此collect
    }

}
