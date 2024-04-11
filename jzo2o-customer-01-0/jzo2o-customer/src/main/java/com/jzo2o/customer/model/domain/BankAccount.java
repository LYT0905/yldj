package com.jzo2o.customer.model.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("bank_account")
public class BankAccount implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_ID)
    /**
    * 服务人员/机构id
    */
    private Long id;

    /**
    * 类型，2：服务人员，3：服务机构
    */
    private Integer type;


    /**
     * 用户id
     */
    private Long userId;

    /**
    * 名称
    */
    private String name;

    /**
    * 银行名称
    */
    private String bankName;

    /**
    * 省
    */
    private String province;

    /**
    * 市
    */
    private String city;

    /**
    * 区
    */
    private String district;

    /**
    * 网点
    */
    private String branch;

    /**
    * 银行账号
    */
    private String account;

    /**
    * 开户证明
    */
    private String accountCertification;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 创建者
     */
    @TableField(fill = FieldFill.INSERT)
    private Long createBy;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 更新者
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateBy;
}