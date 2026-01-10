/**
 * @作者 7七月
 * @微信公号 林间有风
 * @开源项目 $ http://talelin.com
 * @免费专栏 $ http://course.talelin.com
 * @我的课程 $ http://imooc.com/t/4294850
 * @创建时间 2019-07-07 02:10
 */
package com.lin.missyou.model;

import com.lin.missyou.util.MapAndJson;
import lombok.*;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.Map;

import static javax.persistence.GenerationType.IDENTITY;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Setter
@Builder
@Where(clause = "delete_time is null")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    private String openid;

    private String nickname;

    private String email;

    private String mobile;

    private String password; //不建议保存在User表

    private Long unifyUid; // 微信的unionId

//    private String group; //VIP分级

//    @ManyToMany(fetch = FetchType.LAZY) //优惠券不是订单, 而是一个优惠券模版
//    @JoinTable(name = "UserCoupon",
//            joinColumns = @JoinColumn(name = "userId"),
//            inverseJoinColumns = @JoinColumn(name = "couponId"))
//    private List<Coupon> couponList;



    @Convert(converter = MapAndJson.class)
    @Transient
    private Map<String, Object> wxProfile; //JSON类型

    //    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true,mappedBy = "user", fetch = FetchType.LAZY)

//    @OneToMany //一个用户下了多个订单
//    @JoinColumn(name="userId")
//    private List<Order> orders = new ArrayList<>();


//    private String
}
