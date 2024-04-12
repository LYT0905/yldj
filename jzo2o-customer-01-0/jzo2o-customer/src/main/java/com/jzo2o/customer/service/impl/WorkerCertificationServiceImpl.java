package com.jzo2o.customer.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jzo2o.common.expcetions.CommonException;
import com.jzo2o.customer.enums.CertificationStatusEnum;
import com.jzo2o.customer.mapper.WorkerCertificationMapper;
import com.jzo2o.customer.model.domain.WorkerCertification;
import com.jzo2o.customer.model.dto.WorkerCertificationUpdateDTO;
import com.jzo2o.customer.model.dto.request.WorkerCertificationAuditAddReqDTO;
import com.jzo2o.customer.model.dto.response.WorkerCertificationResDTO;
import com.jzo2o.customer.service.IWorkerCertificationService;
import com.jzo2o.mvc.utils.UserContext;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务人员认证信息表 服务实现类
 * </p>
 *
 * @author itcast
 * @since 2023-09-06
 */
@Service
public class WorkerCertificationServiceImpl extends ServiceImpl<WorkerCertificationMapper, WorkerCertification> implements IWorkerCertificationService {




    /**
     * 根据服务人员id更新
     *
     * @param workerCertificationUpdateDTO 服务人员认证更新模型
     */
    @Override
    public void updateById(WorkerCertificationUpdateDTO workerCertificationUpdateDTO) {
        LambdaUpdateWrapper<WorkerCertification> updateWrapper = Wrappers.<WorkerCertification>lambdaUpdate()
                .eq(WorkerCertification::getId, workerCertificationUpdateDTO.getId())
                .set(WorkerCertification::getCertificationStatus, workerCertificationUpdateDTO.getCertificationStatus())
                .set(ObjectUtil.isNotEmpty(workerCertificationUpdateDTO.getName()), WorkerCertification::getName, workerCertificationUpdateDTO.getName())
                .set(ObjectUtil.isNotEmpty(workerCertificationUpdateDTO.getIdCardNo()), WorkerCertification::getIdCardNo, workerCertificationUpdateDTO.getIdCardNo())
                .set(ObjectUtil.isNotEmpty(workerCertificationUpdateDTO.getFrontImg()), WorkerCertification::getFrontImg, workerCertificationUpdateDTO.getFrontImg())
                .set(ObjectUtil.isNotEmpty(workerCertificationUpdateDTO.getBackImg()), WorkerCertification::getBackImg, workerCertificationUpdateDTO.getBackImg())
                .set(ObjectUtil.isNotEmpty(workerCertificationUpdateDTO.getCertificationMaterial()), WorkerCertification::getCertificationMaterial, workerCertificationUpdateDTO.getCertificationMaterial())
                .set(ObjectUtil.isNotEmpty(workerCertificationUpdateDTO.getCertificationTime()), WorkerCertification::getCertificationTime, workerCertificationUpdateDTO.getCertificationTime());
        super.update(updateWrapper);
    }

    /**
     * 提交认证申请
     * @param workerCertificationAuditAddReqDTO 请求参数
     */
    @Override
    public void submit(WorkerCertificationAuditAddReqDTO workerCertificationAuditAddReqDTO) {
        LambdaQueryWrapper<WorkerCertification> queryWrapper = Wrappers.lambdaQuery(WorkerCertification.class)
                .eq(WorkerCertification::getId, UserContext.currentUserId());
        WorkerCertification workerCertification = baseMapper.selectOne(queryWrapper);
        // 判断是否认证成功
        if (ObjectUtil.equals(workerCertification, CertificationStatusEnum.SUCCESS.getStatus())){
            return;
        }
        // 提交认证
        WorkerCertification certification = BeanUtil.toBean(workerCertificationAuditAddReqDTO, WorkerCertification.class);
        certification.setId(UserContext.currentUserId());
        certification.setCertificationStatus(CertificationStatusEnum.PROGRESSING.getStatus());
        int insert = baseMapper.insert(certification);
        if (insert < 1){
            throw new CommonException("提交认证失败");
        }
    }
}
