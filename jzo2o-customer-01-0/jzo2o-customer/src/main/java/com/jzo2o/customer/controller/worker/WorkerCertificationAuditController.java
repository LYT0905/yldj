package com.jzo2o.customer.controller.worker;

import com.jzo2o.customer.model.domain.WorkerCertification;
import com.jzo2o.customer.model.dto.request.WorkerCertificationAuditAddReqDTO;
import com.jzo2o.customer.model.dto.response.RejectReasonResDTO;
import com.jzo2o.customer.service.IWorkerCertificationAuditService;
import com.jzo2o.customer.service.IWorkerCertificationService;
import com.jzo2o.mvc.model.Result;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author LYT0905
 * @date 2024/04/12/13:22
 */

@RestController("workerCertificationAuditController")
@RequestMapping("/worker/worker-certification-audit")
@Api(tags = "服务端-提交认证接口设计")
public class WorkerCertificationAuditController {

    @Resource
    private IWorkerCertificationAuditService workerCertificationAuditService;

    /**
     * 提交认证申请
     * @param workerCertificationAuditAddReqDTO 请求参数
     * @return 响应结果
     */
    @PostMapping()
    public Result<Object> submit(@RequestBody WorkerCertificationAuditAddReqDTO workerCertificationAuditAddReqDTO){
        workerCertificationAuditService.submitWorkerCertificationAudit(workerCertificationAuditAddReqDTO);
        return Result.ok(null);
    }

    /**
     * 查询最新的驳回原因
     * @return 响应结果
     */
    @GetMapping("/rejectReason")
    public RejectReasonResDTO rejectReason(){
        return workerCertificationAuditService.getRejectReason();
    }
}
