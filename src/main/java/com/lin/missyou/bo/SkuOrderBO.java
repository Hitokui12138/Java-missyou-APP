package com.lin.missyou.bo;

import com.lin.missyou.dto.SkuInfoDTO;
import com.lin.missyou.model.Sku;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Coupon验证用
 */
@Getter
@Setter
public class SkuOrderBO {
    private BigDecimal actualPrice;
    private Integer count;
    private Long categoryId;

    public SkuOrderBO(Sku sku, SkuInfoDTO skuInfoDTO) {
        this.actualPrice = sku.getActualPrice();
        this.count = skuInfoDTO.getCount();;
        this.categoryId = sku.getCategoryId();
    }

    public BigDecimal getTotalPrice(){
        return this.getActualPrice().multiply(new BigDecimal(this.getCount()));
    }
}
