package com.jzo2o.customer.controller.open;

import com.jzo2o.customer.model.dto.request.InstitutionRegisterReqDTO;
import com.jzo2o.customer.service.IServeProviderService;
import com.jzo2o.mvc.model.Result;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author LYT0905
 * @date 2024/04/09/21:38
 */

@RestController("openServeProvideController")
@RequestMapping("/open/serve-provider")
public class ServeProvideController {
    @Resource
    private IServeProviderService serveProviderService;

    /**
     * 机构注册
     * @param institutionRegisterReqDTO 请求参数（手机号，验证码，密码）
     */
    @PostMapping("/institution/register")
    public Result<Object> institutionRegister(@RequestBody InstitutionRegisterReqDTO institutionRegisterReqDTO){
        serveProviderService.institutionRegister(institutionRegisterReqDTO);
        return Result.ok(null);
    }
}
