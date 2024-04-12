package com.jzo2o.customer.controller.agency;

import com.jzo2o.customer.model.dto.request.BankAccountUpsertReqDTO;
import com.jzo2o.customer.model.dto.response.BankAccountResDTO;
import com.jzo2o.customer.service.IBankAccountService;
import com.jzo2o.mvc.model.Result;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author LYT0905
 * @date 2024/04/12/12:55
 */

@RequestMapping("/agency/bank-account")
@RestController
@Api(tags = "机构端-设置银行账户接口设计")
public class BankAccountController {

    @Resource
    private IBankAccountService bankAccountService;

    /**
     * 新增或更新银行账号信息
     * @param bankAccountUpsertReqDTO 请求参数
     * @return 响应结果
     */
    @PostMapping()
    public Result<Object> saveOrUpdate(@RequestBody BankAccountUpsertReqDTO bankAccountUpsertReqDTO){
        bankAccountService.saveOrUpdateBankAccount(bankAccountUpsertReqDTO);
        return Result.ok(null);
    }

    /**
     *  获取当前用户银行账号接口
     * @return 响应参数
     */
    @GetMapping("/currentUserBankAccount")
    public BankAccountResDTO currentUserBankAccount(){
        return bankAccountService.getCurrentUserBankAccount();
    }
}
