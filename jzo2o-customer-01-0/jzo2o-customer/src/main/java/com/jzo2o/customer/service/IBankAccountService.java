package com.jzo2o.customer.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jzo2o.customer.model.domain.BankAccount;
import com.jzo2o.customer.model.dto.request.BankAccountUpsertReqDTO;
import com.jzo2o.customer.model.dto.response.BankAccountResDTO;

/**
 * @author LYT0905
 * @date 2024/04/11/20:10
 */
public interface IBankAccountService extends IService<BankAccount> {
    /**
     * 新增或更新银行账号信息接口
     * @param bankAccountUpsertReqDTO 请求参数
     */
    void saveOrUpdateBankAccount(BankAccountUpsertReqDTO bankAccountUpsertReqDTO);

    /**
     * 获取当前用户银行账号
     * @return 响应参数
     */
    BankAccountResDTO getCurrentUserBankAccount();
}
