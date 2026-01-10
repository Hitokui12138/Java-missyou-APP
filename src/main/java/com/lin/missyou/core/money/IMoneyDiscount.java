package com.lin.missyou.core.money;

import java.math.BigDecimal;

/**
 * 用于进行金额计算
 */

public interface IMoneyDiscount {
    BigDecimal discount(BigDecimal original, BigDecimal discount);
}
