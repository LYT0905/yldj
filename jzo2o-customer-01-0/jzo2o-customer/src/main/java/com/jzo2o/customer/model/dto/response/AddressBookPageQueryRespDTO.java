package com.jzo2o.customer.model.dto.response;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * @author LYT0905
 * @date 2024/04/11/13:09
 */
@Data
public class AddressBookPageQueryRespDTO {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 名称
     */
    private String name;

    /**
     * 电话
     */
    private String phone;

    /**
     * 省份
     */
    private String province;

    /**
     * 市级
     */
    private String city;

    /**
     * 区/县
     */
    private String county;

    /**
     * 详细地址
     */
    private String address;

    /**
     * 经度
     */
    private Double lon;

    /**
     * 纬度
     */
    private Double lat;

    /**
     * 是否为默认地址，0：否，1：是
     */
    private Integer isDefault;

}
