package com.jzo2o.customer.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author LYT0905
 * @date 2024/04/10/22:21
 */


@Getter
@AllArgsConstructor
public enum AddressBookStatusEnum {
    IS_NOT_DEFAULT(0, "非默认地址"),
    IS_DEFAULT(1, "默认地址");

    /**
     * 状态值
     */
    private final int status;

    /**
     * 描述
     */
    private final String description;
}
