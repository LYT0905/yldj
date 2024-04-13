package com.jzo2o.customer.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jzo2o.customer.model.domain.AgencyCertificationAudit;
import com.jzo2o.customer.model.dto.request.AgencyCertificationAuditAddReqDTO;
import org.springframework.stereotype.Service;

/**
 * @author LYT0905
 * @date 2024/04/13/12:52
 */
public interface AgencyCertificationAuditService extends IService<AgencyCertificationAudit> {
    /**
     * 机构提交认证申请
     * @param agencyCertificationAuditAddReqDTO 请求参数
     */
    void submitAgencyCertificationAudit(AgencyCertificationAuditAddReqDTO agencyCertificationAuditAddReqDTO);
}
