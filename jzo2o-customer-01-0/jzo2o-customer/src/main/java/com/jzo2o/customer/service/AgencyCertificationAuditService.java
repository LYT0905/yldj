package com.jzo2o.customer.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jzo2o.common.model.PageResult;
import com.jzo2o.customer.model.domain.AgencyCertificationAudit;
import com.jzo2o.customer.model.dto.request.AgencyCertificationAuditAddReqDTO;
import com.jzo2o.customer.model.dto.request.AgencyCertificationAuditPageQueryReqDTO;
import com.jzo2o.customer.model.dto.response.AgencyCertificationAuditResDTO;
import com.jzo2o.customer.model.dto.response.RejectReasonResDTO;
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

    /**
     * 查询最新的驳回原因
     * @return 响应参数
     */
    RejectReasonResDTO getRejectReason();

    /**
     * 审核机构认证分页查询
     * @param agencyCertificationAuditPageQueryReqDTO 请求参数
     * @return 响应结果
     */
    PageResult<AgencyCertificationAuditResDTO> pageQuery(AgencyCertificationAuditPageQueryReqDTO agencyCertificationAuditPageQueryReqDTO);
}
