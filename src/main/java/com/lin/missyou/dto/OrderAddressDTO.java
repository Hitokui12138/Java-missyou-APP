package com.lin.missyou.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 这里主要是参考微信能读取的用户信息的数据结构
 */
@Getter
@Setter
public class OrderAddressDTO {
    private String userName;
    private String province;
    private String city;
    private String country;
    private String mobile;
    private String nationalCode;
    private String postalCode;
    private String detail;

}
