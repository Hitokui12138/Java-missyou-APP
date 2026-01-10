package com.lin.missyou.bo;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * 瀑布流API接收的是start和count
 * 转换成Pageable需要的page,size
 * 需要两个数据, Java是面向对象的, 只能用这个类来包装这两个属性
 */
@Getter
@Setter
@Builder
public class PageCounter {
    private Integer page;
    private Integer count;
}
