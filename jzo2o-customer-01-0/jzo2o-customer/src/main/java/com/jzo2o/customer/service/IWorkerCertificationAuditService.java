package com.jzo2o.customer.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jzo2o.customer.model.domain.WorkerCertificationAudit;
import com.jzo2o.customer.model.dto.request.WorkerCertificationAuditAddReqDTO;
import com.jzo2o.customer.model.dto.response.RejectReasonResDTO;

/**
 * @author LYT0905
 * @date 2024/04/12/16:46
 */
public interface IWorkerCertificationAuditService extends IService<WorkerCertificationAudit> {
    /**
     * 提交认证申请
     * @param workerCertificationAuditAddReqDTO 请求参数
     * @return 响应结果
     */
    void submit(WorkerCertificationAuditAddReqDTO workerCertificationAuditAddReqDTO);

    RejectReasonResDTO getRejectReason();
}
