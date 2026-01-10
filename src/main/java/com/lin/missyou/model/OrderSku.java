/**
 * @作者 7七月
 * @微信公号 林间有风
 * @开源项目 $ http://talelin.com
 * @免费专栏 $ http://course.talelin.com
 * @我的课程 $ http://imooc.com/t/4294850
 * @创建时间 2020-03-27 15:34
 */
package com.lin.missyou.model;

import com.lin.missyou.dto.SkuInfoDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * 这个只是个中间模型, 并不对应表, 因此不需要@Entity
 */
@Getter
@Setter
@NoArgsConstructor //序列化需要这个无参构造函数
public class OrderSku {
    private Long id;
    private Long spuId;
    private BigDecimal finalPrice;//单价*数量
    private BigDecimal singlePrice;//一个Sku的单价
    private List<String> specValues;
    private Integer count;
    private String img;
    private String title;

    public OrderSku(Sku sku, SkuInfoDTO skuInfoDTO) {

        this.id = sku.getId();
        this.spuId = sku.getSpuId();
        this.singlePrice = sku.getActualPrice();//价格应该从数据库获取
        this.finalPrice = sku.getActualPrice().multiply(new BigDecimal(skuInfoDTO.getCount()));
        this.count = skuInfoDTO.getCount();
        this.img = sku.getImg();
        this.title = sku.getTitle();
        this.specValues = sku.getSpecValueList();
    }
}
