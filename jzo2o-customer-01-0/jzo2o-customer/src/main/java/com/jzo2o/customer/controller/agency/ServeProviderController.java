package com.jzo2o.customer.controller.agency;


import com.jzo2o.customer.model.dto.request.InstitutionRegisterReqDTO;
import com.jzo2o.customer.model.dto.request.InstitutionResetPasswordReqDTO;
import com.jzo2o.customer.model.dto.response.ServeProviderInfoResDTO;
import com.jzo2o.customer.service.IServeProviderService;
import com.jzo2o.mvc.model.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * <p>
 * 服务人员/机构表 前端控制器
 * </p>
 *
 * @author itcast
 * @since 2023-07-17
 */
@RestController("agencyServeProviderController")
@RequestMapping("/agency/serve-provider")
@Api(tags = "机构端 - 服务人员或机构相关接口")
public class ServeProviderController {
    @Resource
    private IServeProviderService serveProviderService;

    @GetMapping("/currentUserInfo")
    @ApiOperation("获取当前用户信息")
    public ServeProviderInfoResDTO currentUserInfo() {
        return serveProviderService.currentUserInfo();
    }

    /**
     * 重置密码
     * @param institutionRegisterReqDTO 请求参数（手机号，验证码，密码）
     */
    @PostMapping("/institution/resetPassword")
    public Result<Object> resetPassword(@RequestBody InstitutionRegisterReqDTO institutionRegisterReqDTO){
        serveProviderService.resetPassword(institutionRegisterReqDTO);
        return Result.ok("重置密码成功");
    }
}
