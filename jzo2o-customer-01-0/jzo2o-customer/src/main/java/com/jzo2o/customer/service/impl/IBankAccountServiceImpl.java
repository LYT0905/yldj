package com.jzo2o.customer.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.BeanUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jzo2o.customer.mapper.BankAccountMapper;
import com.jzo2o.customer.model.domain.BankAccount;
import com.jzo2o.customer.model.dto.request.BankAccountUpsertReqDTO;
import com.jzo2o.customer.model.dto.response.BankAccountResDTO;
import com.jzo2o.customer.service.IBankAccountService;
import com.jzo2o.mvc.utils.UserContext;
import org.springframework.stereotype.Service;

/**
 * @author LYT0905
 * @date 2024/04/11/20:10
 */

@Service
public class IBankAccountServiceImpl extends ServiceImpl<BankAccountMapper, BankAccount> implements IBankAccountService {
    /**
     * 新增或更新银行账号信息接口
     * @param bankAccountUpsertReqDTO 请求参数
     */
    @Override
    public BankAccountResDTO saveOrUpdateBankAccount(BankAccountUpsertReqDTO bankAccountUpsertReqDTO) {
        LambdaUpdateWrapper<BankAccount> queryWrapper = Wrappers.lambdaUpdate(BankAccount.class)
                .eq(BankAccount::getUserId, UserContext.currentUserId())
                .eq(ObjectUtil.isNotNull(bankAccountUpsertReqDTO.getId()), BankAccount::getId, bankAccountUpsertReqDTO.getId());
        BankAccount bankAccount = baseMapper.selectOne(queryWrapper);
        if (ObjectUtil.isNotNull(bankAccount)){
            // 更新操作
            baseMapper.update(BeanUtil.toBean(bankAccountUpsertReqDTO, BankAccount.class), queryWrapper);
            return BeanUtil.toBean(bankAccount, BankAccountResDTO.class);
        }
        // 新增操作
        bankAccount = BeanUtil.toBean(bankAccountUpsertReqDTO, BankAccount.class);
        bankAccount.setUserId(UserContext.currentUserId());
        baseMapper.insert(bankAccount);
        return BeanUtil.toBean(bankAccount, BankAccountResDTO.class);
    }

    /**
     * 获取当前用户银行账号
     * @return 响应参数
     */
    @Override
    public BankAccountResDTO getCurrentUserBankAccount() {
        LambdaQueryWrapper<BankAccount> queryWrapper = Wrappers.lambdaQuery(BankAccount.class)
                .eq(BankAccount::getUserId, UserContext.currentUserId());
        BankAccount bankAccount = baseMapper.selectOne(queryWrapper);
        if (ObjectUtil.isNull(bankAccount)){
            // 第一次
            return null;
        }
        return BeanUtil.toBean(bankAccount, BankAccountResDTO.class);
    }
}
