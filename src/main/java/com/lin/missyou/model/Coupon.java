/**
 * @作者 7七月
 * @微信公号 林间有风
 * @开源项目 $ http://talelin.com
 * @免费专栏 $ http://course.talelin.com
 * @我的课程 $ http://imooc.com/t/4294850
 * @创建时间 2019-08-05 05:19
 */
package com.lin.missyou.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@Where(clause = "delete_time is null")
public class Coupon extends BaseEntity {
    @Id
    private Long id;
    private Long activityId;
    private String title;
    private Date startTime; //可以使用的时间
    private Date endTime; // 截止时间
    private String description;
    private BigDecimal fullMoney; // 满减券的满的金额
    private BigDecimal minus; // 减多少
    private BigDecimal rate; // 用于折扣券, 打折rate
    private String remark; // 也是description, 用于前端显示用
    private Boolean wholeStore; // 是不是全场券?
    private Integer type; // 1.满减, 2.折扣, 3.无门槛, 4.满金额折扣

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "couponList")
    private List<Category> categoryList;
}
