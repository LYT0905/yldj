package com.jzo2o.foundations.model.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author LYT0905
 * @date 2024/04/14/15:37
 */

@Data
public class ServeItemSimpleSearchReqDTO {

    /**
     * 城市编码
     */
    @ApiModelProperty(value = "城市编码", required = true)
    private String cityCode;

    @ApiModelProperty("服务类型id")
    private Long serveTypeId;
}
