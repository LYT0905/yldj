package com.jzo2o.customer.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jzo2o.common.model.PageResult;
import com.jzo2o.customer.enums.CertificationStatusEnum;
import com.jzo2o.customer.mapper.AgencyCertificationAuditMapper;
import com.jzo2o.customer.model.domain.AgencyCertification;
import com.jzo2o.customer.model.domain.AgencyCertificationAudit;
import com.jzo2o.customer.model.dto.request.AgencyCertificationAuditAddReqDTO;
import com.jzo2o.customer.model.dto.request.AgencyCertificationAuditPageQueryReqDTO;
import com.jzo2o.customer.model.dto.response.AgencyCertificationAuditResDTO;
import com.jzo2o.customer.model.dto.response.RejectReasonResDTO;
import com.jzo2o.customer.service.AgencyCertificationAuditService;
import com.jzo2o.customer.service.IAgencyCertificationService;
import com.jzo2o.mvc.utils.UserContext;
import com.jzo2o.mysql.utils.PageUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

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

    /**
     * 查询最新的驳回原因
     * @return 响应参数
     */
    @Override
    public RejectReasonResDTO getRejectReason() {
        LambdaQueryWrapper<AgencyCertificationAudit> queryWrapper = Wrappers.lambdaQuery(AgencyCertificationAudit.class)
                .eq(AgencyCertificationAudit::getServeProviderId, UserContext.currentUserId())
                .orderByDesc(AgencyCertificationAudit::getCreateTime)
                .last("limit 1");
        AgencyCertificationAudit agencyCertificationAudit = baseMapper.selectOne(queryWrapper);
        RejectReasonResDTO rejectReasonResDTO = new RejectReasonResDTO();
        rejectReasonResDTO.setRejectReason(agencyCertificationAudit.getRejectReason());
        return rejectReasonResDTO;
    }

    /**
     * 审核机构认证分页查询
     * @param agencyCertificationAuditPageQueryReqDTO 请求参数
     * @return 响应结果
     */
    @Override
    public PageResult<AgencyCertificationAuditResDTO> pageQuery(AgencyCertificationAuditPageQueryReqDTO agencyCertificationAuditPageQueryReqDTO) {
        Page<AgencyCertificationAudit> agencyCertificationAuditPage = PageUtils.parsePageQuery(agencyCertificationAuditPageQueryReqDTO, AgencyCertificationAudit.class);
        LambdaQueryWrapper<AgencyCertificationAudit> queryWrapper = Wrappers.lambdaQuery(AgencyCertificationAudit.class)
                .like(ObjectUtil.isNotEmpty(agencyCertificationAuditPageQueryReqDTO.getName()), AgencyCertificationAudit::getName, agencyCertificationAuditPageQueryReqDTO.getName())
                .like(ObjectUtil.isNotEmpty(agencyCertificationAuditPageQueryReqDTO.getLegalPersonName()), AgencyCertificationAudit::getLegalPersonName, agencyCertificationAuditPageQueryReqDTO.getLegalPersonName())
                .eq(ObjectUtil.isNotEmpty(agencyCertificationAuditPageQueryReqDTO.getAuditStatus()), AgencyCertificationAudit::getAuditStatus, agencyCertificationAuditPageQueryReqDTO.getAuditStatus())
                .eq(ObjectUtil.isNotEmpty(agencyCertificationAuditPageQueryReqDTO.getCertificationStatus()), AgencyCertificationAudit::getCertificationStatus, agencyCertificationAuditPageQueryReqDTO.getCertificationStatus());
        Page<AgencyCertificationAudit> page = baseMapper.selectPage(agencyCertificationAuditPage, queryWrapper);
        return PageUtils.toPage(page, AgencyCertificationAuditResDTO.class);
    }
}
