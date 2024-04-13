package com.jzo2o.customer.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jzo2o.common.model.PageResult;
import com.jzo2o.customer.model.domain.WorkerCertificationAudit;
import com.jzo2o.customer.model.dto.request.AgencyCertificationAuditAddReqDTO;
import com.jzo2o.customer.model.dto.request.CertificationAuditReqDTO;
import com.jzo2o.customer.model.dto.request.WorkerCertificationAuditAddReqDTO;
import com.jzo2o.customer.model.dto.request.WorkerCertificationAuditPageQueryReqDTO;
import com.jzo2o.customer.model.dto.response.RejectReasonResDTO;
import com.jzo2o.customer.model.dto.response.WorkerCertificationAuditResDTO;

/**
 * @author LYT0905
 * @date 2024/04/12/16:46
 */
public interface IWorkerCertificationAuditService extends IService<WorkerCertificationAudit> {
    /**
     * 提交认证申请
     * @param workerCertificationAuditAddReqDTO 请求参数
     */
    void submitWorkerCertificationAudit(WorkerCertificationAuditAddReqDTO workerCertificationAuditAddReqDTO);

    /**
     * 查询最新的驳回原因
     * @return 响应结果
     */
    RejectReasonResDTO getRejectReason();

    /**
     * 审核服务人员认证分页查询
     * @param workerCertificationAuditPageQueryReqDTO 请求参数
     * @return 响应参数
     */
    PageResult<WorkerCertificationAuditResDTO> pageQuery(WorkerCertificationAuditPageQueryReqDTO workerCertificationAuditPageQueryReqDTO);


    void auditCertification(Long id, CertificationAuditReqDTO certificationAuditReqDTO);
}
