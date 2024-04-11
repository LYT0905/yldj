package com.jzo2o.customer.controller.consumer;

import com.jzo2o.customer.model.dto.request.BankAccountUpsertReqDTO;
import com.jzo2o.customer.model.dto.response.BankAccountResDTO;
import com.jzo2o.customer.service.IBankAccountService;
import com.jzo2o.mvc.model.Result;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author LYT0905
 * @date 2024/04/11/20:08
 */
@RestController("workerBankAccountController")
@RequestMapping("/worker/bank-account")
@ApiOperation("服务端设置银行账户接口设计")
public class BankAccountController {
    @Resource
    private IBankAccountService bankAccountService;

    /**
     * 新增或更新银行账号信息接口
     * @param bankAccountUpsertReqDTO 请求参数
     * @return 响应参数
     */
    @PostMapping
    public Result<String> saveOrUpdateBankAccount(@RequestBody BankAccountUpsertReqDTO bankAccountUpsertReqDTO){
        bankAccountService.saveOrUpdateBankAccount(bankAccountUpsertReqDTO);
        return Result.ok("操作成功");
    }

    /**
     * 获取当前用户银行账号
     * @return 响应参数
     */
    @GetMapping("/currentUserBankAccount")
    public BankAccountResDTO currenUserBankAccount(){
        return bankAccountService.getCurrentUserBankAccount();
    }
}
