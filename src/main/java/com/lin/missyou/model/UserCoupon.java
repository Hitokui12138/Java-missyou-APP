/**
 * @作者 7七月
 * @微信公号 林间有风
 * @开源项目 $ http://talelin.com
 * @免费专栏 $ http://course.talelin.com
 * @我的课程 $ http://imooc.com/t/4294850
 * @创建时间 2019-08-19 15:17
 */
package com.lin.missyou.model;


import lombok.*;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Objects;

import static javax.persistence.GenerationType.IDENTITY;

/**
 * 这是一张有业务意义的多对多关系表
 * 表示一个用户使用的优惠券
 */
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCoupon {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    private Long userId;
    private Long couponId;
    private Long orderId; // 该优惠券在哪一张表使用的
    private Integer status; // 该优惠券是否领取,使用, 过期, 数据库使用tinyInt表示枚举
    private Date createTime; // 既表示该数据的创建时间, 也表示用户领取该优惠券的时间
}
