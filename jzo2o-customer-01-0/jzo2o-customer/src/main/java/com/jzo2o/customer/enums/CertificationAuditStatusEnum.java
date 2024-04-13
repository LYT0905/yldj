package com.jzo2o.customer.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author LYT0905
 * @date 2024/04/13/15:07
 */

@Getter
@AllArgsConstructor
public enum CertificationAuditStatusEnum {
    NO_CERTIFICATION(0, "未审核"),
    SUCCESS(1, "已审核");

    /**
     * 状态值
     */
    private final int status;

    /**
     * 描述
     */
    private final String description;
}
