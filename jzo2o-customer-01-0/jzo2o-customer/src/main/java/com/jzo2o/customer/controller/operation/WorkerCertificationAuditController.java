package com.jzo2o.customer.controller.operation;

import com.jzo2o.common.model.PageResult;
import com.jzo2o.customer.model.domain.WorkerCertificationAudit;
import com.jzo2o.customer.model.dto.request.AgencyCertificationAuditPageQueryReqDTO;
import com.jzo2o.customer.model.dto.request.CertificationAuditReqDTO;
import com.jzo2o.customer.model.dto.request.WorkerCertificationAuditPageQueryReqDTO;
import com.jzo2o.customer.model.dto.response.AgencyCertificationAuditResDTO;
import com.jzo2o.customer.model.dto.response.WorkerCertificationAuditResDTO;
import com.jzo2o.customer.service.IWorkerCertificationAuditService;
import com.jzo2o.customer.service.impl.WorkerCertificationAuditServiceImpl;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author LYT0905
 * @date 2024/04/13/15:52
 */

@RestController("operationWorkerCertificationAuditController")
@RequestMapping("/operation/worker-certification-audit")
@Api(tags = "运营端-审核服务人员认证接口设计")
public class WorkerCertificationAuditController {
    @Resource
    private IWorkerCertificationAuditService workerCertificationAuditService;

    /**
     * 审核服务人员认证分页查询
     * @param workerCertificationAuditPageQueryReqDTO 请求参数
     * @return 响应参数
     */
    @GetMapping("/page")
    public PageResult<WorkerCertificationAuditResDTO> page(WorkerCertificationAuditPageQueryReqDTO workerCertificationAuditPageQueryReqDTO){
        return workerCertificationAuditService.pageQuery(workerCertificationAuditPageQueryReqDTO);
    }

    /**
     * 审核服务人员认证信息
     * @param id 申请认证id
     * @param certificationAuditReqDTO 请求参数
     */
    @PutMapping("/audit/{id}")
    public void auditCertification(@PathVariable Long id, CertificationAuditReqDTO certificationAuditReqDTO){
        workerCertificationAuditService.auditCertification(id, certificationAuditReqDTO);
    }
}
