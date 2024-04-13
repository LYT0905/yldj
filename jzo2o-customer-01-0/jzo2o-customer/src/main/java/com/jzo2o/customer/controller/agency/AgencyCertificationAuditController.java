package com.jzo2o.customer.controller.agency;

import com.jzo2o.customer.model.dto.request.AgencyCertificationAuditAddReqDTO;
import com.jzo2o.customer.model.dto.response.RejectReasonResDTO;
import com.jzo2o.customer.service.AgencyCertificationAuditService;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author LYT0905
 * @date 2024/04/13/12:38
 */

@RestController("agencyCertificationAuditController")
@RequestMapping("/agency/agency-certification-audit")
@Api(tags = "机构端-提交认证接口设计")
public class AgencyCertificationAuditController {

    @Resource
    private AgencyCertificationAuditService agencyCertificationAuditService;

    /**
     * 机构提交认证申请
     * @param agencyCertificationAuditAddReqDTO 请求参数
     */
    @PostMapping()
    public void submit(@RequestBody AgencyCertificationAuditAddReqDTO agencyCertificationAuditAddReqDTO){
        agencyCertificationAuditService.submitAgencyCertificationAudit(agencyCertificationAuditAddReqDTO);
    }

    /**
     * 查询最新的驳回原因
     * @return 响应参数
     */
    @GetMapping("/rejectReason")
    public RejectReasonResDTO rejectReason(){
        return agencyCertificationAuditService.getRejectReason();
    }
}
