package com.jzo2o.foundations.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author LYT0905
 * @date 2024/04/08/13:09
 */

@AllArgsConstructor
@Getter
public enum FoundationHotStatusEnum {
    IS_HOT(1,"热门"),
    NOT_HOT(0, "非热门");
    private int status;
    private String description;

    public boolean equals(Integer status) {
        return this.status == status;
    }

    public boolean equals(FoundationHotStatusEnum enableStatusEnum) {
        return enableStatusEnum != null && enableStatusEnum.status == this.getStatus();
    }
}
