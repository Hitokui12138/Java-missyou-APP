package com.lin.missyou.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 前端传进来的Order信息
 */
@Getter
@Setter
public class SkuInfoDTO {
    private Long id;
    private Integer count;
    // 其他信息后段应该根据id去查询, 不能直接采用前端的东西
}
