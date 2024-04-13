package com.jzo2o.customer.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jzo2o.customer.enums.CertificationStatusEnum;
import com.jzo2o.customer.mapper.AgencyCertificationAuditMapper;
import com.jzo2o.customer.model.domain.AgencyCertification;
import com.jzo2o.customer.model.domain.AgencyCertificationAudit;
import com.jzo2o.customer.model.dto.request.AgencyCertificationAuditAddReqDTO;
import com.jzo2o.customer.service.AgencyCertificationAuditService;
import com.jzo2o.customer.service.IAgencyCertificationService;
import com.jzo2o.mvc.utils.UserContext;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author LYT0905
 * @date 2024/04/13/12:53
 */

@Service
public class AgencyCertificationAuditServiceImpl extends ServiceImpl<AgencyCertificationAuditMapper, AgencyCertificationAudit> implements AgencyCertificationAuditService {

    @Resource
    private IAgencyCertificationService agencyCertificationService;

    /**
     * 机构提交认证申请
     * @param agencyCertificationAuditAddReqDTO 请求参数
     */
    @Override
    public void submitAgencyCertificationAudit(AgencyCertificationAuditAddReqDTO agencyCertificationAuditAddReqDTO) {
        Long serverId;
        if (ObjectUtil.isNull(agencyCertificationAuditAddReqDTO.getServeProviderId())){
            serverId = UserContext.currentUserId();
        }else {
            serverId = agencyCertificationAuditAddReqDTO.getServeProviderId();
        }
        AgencyCertificationAudit agencyCertificationAudit = BeanUtil.toBean(agencyCertificationAuditAddReqDTO, AgencyCertificationAudit.class);
        agencyCertificationAudit.setServeProviderId(serverId);
        agencyCertificationAudit.setAuditStatus(CertificationStatusEnum.INIT.getStatus());
        baseMapper.insert(agencyCertificationAudit);

        AgencyCertification agencyCertification = agencyCertificationService.getById(serverId);
        if (ObjectUtil.isNotNull(agencyCertification)){
            agencyCertification.setCertificationStatus(CertificationStatusEnum.PROGRESSING.getStatus());
            agencyCertificationService.updateById(agencyCertification);
        }else {
            agencyCertification = new AgencyCertification()
                    .setId(serverId)
                    .setCertificationStatus(CertificationStatusEnum.PROGRESSING.getStatus())
                    .setIdNumber(agencyCertificationAudit.getIdNumber())
                    .setLegalPersonName(agencyCertificationAudit.getLegalPersonName())
                    .setLegalPersonIdCardNo(agencyCertificationAudit.getLegalPersonIdCardNo())
                    .setBusinessLicense(agencyCertificationAudit.getBusinessLicense())
                    .setName(agencyCertificationAudit.getName());//认证中
            agencyCertificationService.save(agencyCertification);
        }
    }
}
