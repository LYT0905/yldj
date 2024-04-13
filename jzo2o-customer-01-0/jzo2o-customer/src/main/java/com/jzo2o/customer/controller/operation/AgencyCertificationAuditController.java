package com.jzo2o.customer.controller.operation;

import com.jzo2o.common.model.PageResult;
import com.jzo2o.customer.model.dto.request.AgencyCertificationAuditPageQueryReqDTO;
import com.jzo2o.customer.model.dto.request.CertificationAuditReqDTO;
import com.jzo2o.customer.model.dto.response.AgencyCertificationAuditResDTO;
import com.jzo2o.customer.service.AgencyCertificationAuditService;
import io.swagger.annotations.Api;
import io.swagger.models.auth.In;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author LYT0905
 * @date 2024/04/13/14:01
 */

@RestController("operationAgencyCertificationAuditController")
@RequestMapping("/operation/agency-certification-audit")
@Api(tags = "运营端-审核机构认证接口设计")
public class AgencyCertificationAuditController {

    @Resource
    private AgencyCertificationAuditService agencyCertificationAuditService;

    /**
     * 审核机构认证分页查询
     * @param agencyCertificationAuditPageQueryReqDTO 请求参数
     * @return 响应结果
     */
    @GetMapping("/page")
    public PageResult<AgencyCertificationAuditResDTO> page(AgencyCertificationAuditPageQueryReqDTO agencyCertificationAuditPageQueryReqDTO){
        return agencyCertificationAuditService.pageQuery(agencyCertificationAuditPageQueryReqDTO);
    }

    /**
     * 审核机构认证信息
     * @param certificationAuditReqDTO 请求参数
     * @param id 认证申请id
     */
    @PutMapping("/audit/{id}")
    public void agencyCertificationInformation(CertificationAuditReqDTO certificationAuditReqDTO, @PathVariable("id") Long id){
        agencyCertificationAuditService.getAgencyCertificationInformation(certificationAuditReqDTO, id);
    }
}
