package com.lin.missyou.core.enumeration;

import java.util.stream.Stream;

/**
 * 库存应该在什么时候扣减?
 * 预扣除库存, 下单成功, 但没有支付时扣除库存
 */

public enum OrderStatus {
    All(0, "全部"),
    UNPAID(1, "待支付"),// 延迟消息队列来进行倒计时, 但是不能百分百确定更改状态成功
    PAID(2, "已支付"),
    DELIVERED(3, "已发货"),
    FINISHED(4, "已完成"), //用户或者快递员送完货点击
    CANCELED(5, "已取消"), //用户取消订单, 延迟支付到期时系统更新

    // 预扣除库存不存在以下这两种情况
    PAID_BUT_OUT_OF(21, "已支付，但无货或库存不足"),
    DEAL_OUT_OF(22, "已处理缺货但支付的情况");

    private int value;

    OrderStatus(int value, String text) {
        this.value = value;
    }

    public int value() {
        return this.value;
    }

    public static OrderStatus toType(int value) {
        return Stream.of(OrderStatus.values())
                .filter(c -> c.value == value)
                .findAny()
                .orElse(null);
    }
}
