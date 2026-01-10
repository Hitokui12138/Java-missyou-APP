package com.lin.missyou.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * 基础的分页类, 在数据需要分页时使用
 * 不止Spu要用, 因此使用范型
 * Pageing需要返回前端,因为有@Getter@Setter,所以可以序列化
 * @param <T>
 */
@Getter
@Setter
@NoArgsConstructor //加一个无参的构造函数
public class Paging<T> {
    private Long total; //告诉前端总数据量
    private Integer count;
    private Integer page;
    private Integer totalPage;
    private List<T> items;

    //查询到的数据类型是Page类而不是List类
    public Paging(Page<T> pageT){
        initPaeParameter(pageT);
    }

    void initPaeParameter(Page<T> pageT){
        this.total = pageT.getTotalElements();
        this.count = pageT.getSize();
        this.page = pageT.getNumber();
        this.totalPage = pageT.getTotalPages();
    }
}
