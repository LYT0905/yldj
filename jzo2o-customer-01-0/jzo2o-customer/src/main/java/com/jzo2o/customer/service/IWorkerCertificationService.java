package com.jzo2o.customer.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jzo2o.customer.model.domain.WorkerCertification;
import com.jzo2o.customer.model.dto.WorkerCertificationUpdateDTO;
import com.jzo2o.customer.model.dto.request.WorkerCertificationAuditAddReqDTO;

/**
 * <p>
 * 服务人员认证信息表 服务类
 * </p>
 *
 * @author itcast
 * @since 2023-09-06
 */
public interface IWorkerCertificationService extends IService<WorkerCertification> {



    /**
     * 根据服务人员id更新
     *
     * @param workerCertificationUpdateDTO 服务人员认证更新模型
     */
    void updateById(WorkerCertificationUpdateDTO workerCertificationUpdateDTO);

    /**
     * 提交认证申请
     * @param workerCertificationAuditAddReqDTO 请求参数
     */
    void submit(WorkerCertificationAuditAddReqDTO workerCertificationAuditAddReqDTO);
}
