package com.lin.missyou.core.money;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 向上取整数
 * BigDecimal.setScale(保留几位小数, RoundingMode) 默认是四舍五入
 * RoundingMode有八种
 * HALF_UP, HALF_UP
 * 银行家模式是 ROUND_EVEN
 */
@Component
public class HalfUpRound implements IMoneyDiscount{

    @Override
    public BigDecimal discount(BigDecimal original, BigDecimal discount) {
        BigDecimal actualMoney = original.multiply(discount);
        return actualMoney.setScale(2, RoundingMode.HALF_UP);
    }
}
