package com.jzo2o.customer.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jzo2o.api.customer.dto.request.ServerProviderUpdateStatusReqDTO;
import com.jzo2o.api.customer.dto.response.ServeProviderResDTO;
import com.jzo2o.api.customer.dto.response.ServeProviderSimpleResDTO;
import com.jzo2o.api.publics.SmsCodeApi;
import com.jzo2o.api.publics.dto.response.BooleanResDTO;
import com.jzo2o.common.constants.CommonStatusConstants;
import com.jzo2o.common.constants.UserType;
import com.jzo2o.common.enums.EnableStatusEnum;
import com.jzo2o.common.enums.SmsBussinessTypeEnum;
import com.jzo2o.common.expcetions.BadRequestException;
import com.jzo2o.common.expcetions.CommonException;
import com.jzo2o.common.expcetions.ForbiddenOperationException;
import com.jzo2o.common.model.PageResult;
import com.jzo2o.common.utils.BeanUtils;
import com.jzo2o.common.utils.CollUtils;
import com.jzo2o.common.utils.IdUtils;
import com.jzo2o.common.utils.ObjectUtils;
import com.jzo2o.common.utils.*;
import com.jzo2o.customer.mapper.ServeProviderMapper;
import com.jzo2o.customer.model.domain.*;
import com.jzo2o.customer.model.dto.ServeSkillSimpleDTO;
import com.jzo2o.customer.model.dto.request.InstitutionRegisterReqDTO;
import com.jzo2o.customer.model.dto.request.InstitutionResetPasswordReqDTO;
import com.jzo2o.customer.model.dto.request.ServePickUpReqDTO;
import com.jzo2o.customer.model.dto.request.ServeProviderPageQueryReqDTO;
import com.jzo2o.customer.model.dto.response.CertificationStatusDTO;
import com.jzo2o.customer.model.dto.response.ServeProviderBasicInformationResDTO;
import com.jzo2o.customer.model.dto.response.ServeProviderInfoResDTO;
import com.jzo2o.customer.model.dto.response.ServeProviderListResDTO;
import com.jzo2o.customer.service.*;
import com.jzo2o.mvc.utils.UserContext;
import com.jzo2o.mysql.utils.PageHelperUtils;
import javassist.compiler.CompileError;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务人员/机构表 服务实现类
 * </p>
 *
 * @author itcast
 * @since 2023-07-17
 */
@Service
public class ServeProviderServiceImpl extends ServiceImpl<ServeProviderMapper, ServeProvider> implements IServeProviderService {

    @Resource
    private PasswordEncoder passwordEncoder;

    @Resource
    private IServeProviderSettingsService serveProviderSettingsService;

    @Resource
    private IServeProviderSyncService serveProviderSyncService;
    @Resource
    private IAgencyCertificationService agencyCertificationService;
    @Resource
    private IWorkerCertificationService workerCertificationService;

    @Resource
    private IServeProviderService owner;
    @Resource
    private SmsCodeApi smsCodeApi;

    @Override
    public PageResult<ServeProviderListResDTO> pageQueryWorker(ServeProviderPageQueryReqDTO serveProviderPageQueryReqDTO) {
        return PageHelperUtils.selectPage(serveProviderPageQueryReqDTO, () -> baseMapper.queryWorkerList(serveProviderPageQueryReqDTO));
    }

    @Override
    public PageResult<ServeProviderListResDTO> pageQueryAgency(ServeProviderPageQueryReqDTO serveProviderPageQueryReqDTO) {
        return PageHelperUtils.selectPage(serveProviderPageQueryReqDTO, () -> baseMapper.queryAgencyList(serveProviderPageQueryReqDTO));
    }

    /**
     * 更新服务人员/机构状态
     *
     * @param serverProviderUpdateStatusReqDTO 服务人员/机构更新状态请求
     */
    @Override
    @Transactional
    public void updateStatus(ServerProviderUpdateStatusReqDTO serverProviderUpdateStatusReqDTO) {
        ServeProvider serveProvider = BeanUtil.toBean(serverProviderUpdateStatusReqDTO, ServeProvider.class);
        baseMapper.updateById(serveProvider);

        // 3.同步es
        ServeProviderSync serveProviderSync = ServeProviderSync.builder().id(serverProviderUpdateStatusReqDTO.getId())
                .status(serverProviderUpdateStatusReqDTO.getStatus())
                .build();
        serveProviderSyncService.updateById(serveProviderSync);

        //如果更新为冻结状态，且服务人员/机构为开启接单状态，则需要设置为休息状态
        ServeProviderSettings serveProviderSettings = serveProviderSettingsService.getById(serverProviderUpdateStatusReqDTO.getId());
        Integer canPickUp = serveProviderSettings.getCanPickUp();
        if (ObjectUtils.equal(canPickUp, 1) && ObjectUtils.equal(serverProviderUpdateStatusReqDTO.getStatus(), 1)) {
            serveProviderSettingsService.setPickUp(serverProviderUpdateStatusReqDTO.getId(), 0);
        }
    }

    @Override
    public ServeProvider findByPhoneAndType(String phone, Integer type) {
        return lambdaQuery().eq(ServeProvider::getPhone, phone)
                .eq(ServeProvider::getType, type).one();
    }

    @Override
    public ServeProvider findById(Long id) {
        return baseMapper.selectById(id);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServeProvider add(String phone, Integer type, String password) {
        // 校验手机号是否存在
        ServeProvider existServeProvider = lambdaQuery().eq(ServeProvider::getPhone, phone)
                .one();
        if (existServeProvider != null) {
            if(existServeProvider.getType().equals(UserType.WORKER)){
                throw new BadRequestException("该账号已被服务人员注册");
            }else {
                throw new BadRequestException("该账号已被机构注册");
            }
        }

        //新增服务人员/机构信息
        ServeProvider serveProvider = new ServeProvider();
        serveProvider.setPhone(phone);
        serveProvider.setPassword(password);
        serveProvider.setType(type);
        serveProvider.setStatus(CommonStatusConstants.USER_STATUS_NORMAL);
        serveProvider.setCode(IdUtils.getSnowflakeNextIdStr());
        baseMapper.insert(serveProvider);

        //新增服务人员/机构配置信息同步表信息,方便后期对配置项进行配置
        serveProviderSettingsService.add(serveProvider.getId(), type);

        return serveProvider;
    }

    /**
     * 机构注册
     * @param institutionRegisterReqDTO 请求参数（手机号，验证码，密码）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void institutionRegister(InstitutionRegisterReqDTO institutionRegisterReqDTO) {
        if (ObjectUtil.isNull(institutionRegisterReqDTO.getPhone()) ||
                ObjectUtil.isNull(institutionRegisterReqDTO.getPassword()) ||
                ObjectUtil.isNull(institutionRegisterReqDTO.getVerifyCode())){
            throw new ForbiddenOperationException("请输入合法参数");
        }
        LambdaQueryWrapper<ServeProvider> eq = Wrappers.lambdaQuery(ServeProvider.class)
                .eq(ServeProvider::getPhone, institutionRegisterReqDTO.getPhone());
        ServeProvider serveProvider = baseMapper.selectOne(eq);
        if (ObjectUtil.isNotNull(serveProvider)){
            if (ObjectUtil.equals(serveProvider.getType(), UserType.WORKER)){
                throw new BadRequestException("该账号已被服务人员注册");
            }else {
                throw new BadRequestException("该账号已被机构注册");
            }
        }
        // 调用public校验验证码
        BooleanResDTO verify = smsCodeApi.verify(institutionRegisterReqDTO.getPhone(),
                SmsBussinessTypeEnum.INSTITION_REGISTER, institutionRegisterReqDTO.getVerifyCode());
        if (BooleanUtil.isFalse(verify.getIsSuccess())){
            throw new ForbiddenOperationException("验证码填写错误，请重试");
        }
        String passwordEncode = passwordEncoder.encode(institutionRegisterReqDTO.getPassword());
        ServeProvider institutionServeProvider = new ServeProvider()
                .setPhone(institutionRegisterReqDTO.getPhone())
                .setType(UserType.INSTITUTION)
                .setStatus(CommonStatusConstants.USER_STATUS_NORMAL)
                .setCode(IdUtils.getSnowflakeNextIdStr())
                .setPassword(passwordEncode);
        int insert = baseMapper.insert(institutionServeProvider);
        if (insert < 1){
            throw new CommonException("注册失败");
        }
        //新增服务人员/机构配置信息同步表信息,方便后期对配置项进行配置
        serveProviderSettingsService.add(institutionServeProvider.getId(), UserType.INSTITUTION);
    }

    @Override
    public ServeProviderResDTO findServeProviderInfo(Long id) {
        ServeProvider serveProvider = this.findById(id);
        ServeProviderResDTO serveProviderResDTO = BeanUtils.toBean(serveProvider, ServeProviderResDTO.class);

        ServeProviderSettings serveProviderSettings = serveProviderSettingsService.findById(id);
        // location
        serveProviderResDTO.setLat(serveProviderSettings.getLat());
        serveProviderResDTO.setLon(serveProviderSettings.getLon());
        // cityCode
        serveProviderResDTO.setCityCode(serveProviderSettings.getCityCode());
        // 是否开启接单
        serveProviderResDTO.setCanPickUp(EnableStatusEnum.ENABLE.equals(serveProviderSettings.getCanPickUp()));

        //获取认证状态
        CertificationStatusDTO certificationStatusDTO = getCertificationStatus(serveProvider.getType(), id);
        //获取认证状态
        Integer certificationStatus = ObjectUtils.get(certificationStatusDTO,CertificationStatusDTO::getCertificationStatus);
        serveProviderResDTO.setVerifyStatus(certificationStatus);

        return serveProviderResDTO;
    }

    /**
     * 根据id更新名称
     *
     * @param id   服务人员/机构id
     * @param name 名称
     */
    @Override
    public void updateNameById(Long id, String name) {
        LambdaUpdateWrapper<ServeProvider> updateWrapper = Wrappers.<ServeProvider>lambdaUpdate()
                .eq(ServeProvider::getId, id)
                .set(ServeProvider::getName, name);
        super.update(updateWrapper);
    }

    /**
     * 获取服务人员或机构的信息状态
     * @param userType
     * @param providerId
     * @return
     */
    @Override
    public CertificationStatusDTO getCertificationStatus(Integer userType, Long providerId){

        if (ObjectUtil.equal(UserType.WORKER, userType)) {
            WorkerCertification workerCertification = workerCertificationService.getById(providerId);
            return BeanUtil.toBean(workerCertification,CertificationStatusDTO.class);
        } else {
            AgencyCertification agencyCertification = agencyCertificationService.getById(providerId);
            return BeanUtil.toBean(agencyCertification,CertificationStatusDTO.class);
        }
    }

    /**
     * 根据服务人员/机构id查询基本信息
     *
     * @param id 服务人员/机构id
     * @return 基本信息
     */
    @Override
    public ServeProviderBasicInformationResDTO findBasicInformationById(Long id) {
        ServeProviderBasicInformationResDTO serveProviderBasicInformationResDTO = baseMapper.findBasicInformationById(id);

        //获取认证状态
        CertificationStatusDTO certificationStatus = getCertificationStatus(serveProviderBasicInformationResDTO.getType(), id);
        //获取认证时间
        LocalDateTime certificationTime = ObjectUtils.get(certificationStatus,CertificationStatusDTO::getCertificationTime);
        serveProviderBasicInformationResDTO.setCertificationTime(certificationTime);

        //如果没有服务技能，直接返回
        List<ServeSkillSimpleDTO> serveSkillSimpleList = serveProviderBasicInformationResDTO.getServeSkillSimpleList();
        if (CollUtil.isEmpty(serveSkillSimpleList)) {
            return serveProviderBasicInformationResDTO;
        }

        //处理服务类型、服务项名称，去重之后以顿号分割
        List<String> serveTypeNameList = serveSkillSimpleList.stream().map(ServeSkillSimpleDTO::getServeTypeName).filter(Objects::nonNull).distinct().collect(Collectors.toList());
        List<String> serveItemNameList = serveSkillSimpleList.stream().map(ServeSkillSimpleDTO::getServeItemName).filter(Objects::nonNull).distinct().collect(Collectors.toList());
        serveProviderBasicInformationResDTO.setServeTypes(String.join("、", serveTypeNameList));
        serveProviderBasicInformationResDTO.setServeSkills(String.join("、", serveItemNameList));
        return serveProviderBasicInformationResDTO;
    }

    /**
     * 根据id更新评分数据
     *
     * @param id            服务人员/机构id
     * @param score         综合分数
     * @param goodLevelRate 好评率
     */
    @Override
    public void updateScoreById(Long id, Double score, String goodLevelRate) {
        LambdaUpdateWrapper<ServeProvider> updateWrapper = Wrappers.<ServeProvider>lambdaUpdate()
                .eq(ServeProvider::getId, id)
                .set(null != score, ServeProvider::getScore, score)
                .set(null != goodLevelRate, ServeProvider::getGoodLevelRate, goodLevelRate);
        super.update(updateWrapper);
        // 同步评分
        serveProviderSyncService.updateScore(id, NumberUtils.null2Zero(score));
    }
    @Override
    public List<ServeProviderSimpleResDTO> batchGet(List<Long> ids) {

        List<ServeProvider> serveProviders = baseMapper.selectBatchIds(ids);
        if(CollUtils.isEmpty(serveProviders)) {
            return new ArrayList<>();
        }
        return BeanUtils.copyToList(serveProviders, ServeProviderSimpleResDTO.class);
    }

    /**
     * 获取当前用户信息
     *
     * @return 用户信息
     */
    @Override
    public ServeProviderInfoResDTO currentUserInfo() {
        ServeProvider serveProvider = baseMapper.selectById(UserContext.currentUserId());
        return BeanUtils.toBean(serveProvider,ServeProviderInfoResDTO.class);
    }

    /**
     * 重置密码
     * @param institutionRegisterReqDTO 请求参数（手机号，验证码，密码）
     */
    @Override
    public void resetPassword(InstitutionRegisterReqDTO institutionRegisterReqDTO) {
        if (ObjectUtil.isNull(institutionRegisterReqDTO.getPhone()) ||
                ObjectUtil.isNull(institutionRegisterReqDTO.getPassword()) ||
                ObjectUtil.isNull(institutionRegisterReqDTO.getVerifyCode())){
            throw new ForbiddenOperationException("请输入合法参数");
        }
        LambdaQueryWrapper<ServeProvider> eq = Wrappers.lambdaQuery(ServeProvider.class)
                .eq(ServeProvider::getPhone, institutionRegisterReqDTO.getPhone());
        ServeProvider serveProvider = baseMapper.selectOne(eq);
        if (ObjectUtil.isNull(serveProvider)){
            throw new ForbiddenOperationException("请重新输入手机号");
        }
        if (ObjectUtil.notEqual(serveProvider.getType(), UserType.INSTITUTION)){
            throw new ForbiddenOperationException("请输入合法手机号");
        }
        BooleanResDTO verify = smsCodeApi.verify(institutionRegisterReqDTO.getPhone(),
                SmsBussinessTypeEnum.INSTITUTION_RESET_PASSWORD, institutionRegisterReqDTO.getVerifyCode());
        if (BooleanUtil.isFalse(verify.getIsSuccess())){
            throw new ForbiddenOperationException("验证码错误，请重新输入");
        }
        LambdaUpdateWrapper<ServeProvider> updateWrapper = Wrappers.lambdaUpdate(ServeProvider.class)
                .eq(ServeProvider::getPhone, institutionRegisterReqDTO.getPhone())
                .eq(ServeProvider::getType, UserType.INSTITUTION);
        String encodePassword = passwordEncoder.encode(institutionRegisterReqDTO.getPassword());
        ServeProvider updateServeProvide = new ServeProvider()
                .setPassword(encodePassword);
        int update = baseMapper.update(updateServeProvide, updateWrapper);
        if (update < 1){
            throw new CommonException("重置密码失败");
        }
    }
}
