package com.lin.missyou.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * 这里为什么接收前端传来的价格信息?
 * 因为价格可能实时变动, 若与前端价格不符, 应该拒绝这笔订单
 */

@Getter
@Setter
public class OrderDTO {

    private BigDecimal totalPrice;//原价

    private  BigDecimal finalTotalPrice;//最终价格

    private Long couponId;

    private List<SkuInfoDTO> skuInfoList;

    private OrderAddressDTO address;
}
