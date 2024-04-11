package com.jzo2o.customer.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author LYT0905
 * @date 2024/04/11/13:17
 */

@Getter
@AllArgsConstructor
public enum AddressBookIsDeletedStatusEnum {
    IS_NOT_DELETED(0, "未删除"),
    IS_DELETED(1, "已删除");

    /**
     * 状态值
     */
    private final int status;

    /**
     * 描述
     */
    private final String description;
}
