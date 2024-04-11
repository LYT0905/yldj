package com.jzo2o.customer.model.dto.response;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author LYT0905
 * @date 2024/04/11/18:25
 */

@Data
public class AddressBookDefaultAddressRespDTO {
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

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
