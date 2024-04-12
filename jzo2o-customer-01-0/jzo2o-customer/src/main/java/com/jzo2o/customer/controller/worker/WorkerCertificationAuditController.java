package com.jzo2o.customer.controller.worker;

import com.jzo2o.customer.model.domain.WorkerCertification;
import com.jzo2o.customer.model.dto.request.WorkerCertificationAuditAddReqDTO;
import com.jzo2o.customer.service.IWorkerCertificationService;
import com.jzo2o.mvc.model.Result;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    private IWorkerCertificationService workerCertificationService;

    /**
     * 提交认证申请
     * @param workerCertificationAuditAddReqDTO 请求参数
     * @return 响应结果
     */
    @PostMapping()
    public Result<Object> submit(@RequestBody WorkerCertificationAuditAddReqDTO workerCertificationAuditAddReqDTO){
        workerCertificationService.submit(workerCertificationAuditAddReqDTO);
        return Result.ok(null);
    }
}
