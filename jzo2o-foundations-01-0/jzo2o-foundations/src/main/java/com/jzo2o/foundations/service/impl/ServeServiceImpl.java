package com.jzo2o.foundations.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jzo2o.common.expcetions.CommonException;
import com.jzo2o.common.expcetions.ForbiddenOperationException;
import com.jzo2o.common.model.PageResult;
import com.jzo2o.foundations.enums.FoundationStatusEnum;
import com.jzo2o.foundations.mapper.RegionMapper;
import com.jzo2o.foundations.mapper.ServeItemMapper;
import com.jzo2o.foundations.mapper.ServeMapper;
import com.jzo2o.foundations.model.domain.Region;
import com.jzo2o.foundations.model.domain.Serve;
import com.jzo2o.foundations.model.domain.ServeItem;
import com.jzo2o.foundations.model.dto.request.ServePageQueryReqDTO;
import com.jzo2o.foundations.model.dto.request.ServeUpsertReqDTO;
import com.jzo2o.foundations.model.dto.response.ServeResDTO;
import com.jzo2o.foundations.service.ServeService;
import com.jzo2o.mysql.utils.PageHelperUtils;
import com.jzo2o.mysql.utils.PageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

/**
 * @author LYT0905
 * @date 2024/04/07/20:26
 */

/**
 * 运营端 - 区域服务 Service接口实现层
 */

@Service
@RequiredArgsConstructor
public class ServeServiceImpl extends ServiceImpl<ServeMapper, Serve> implements ServeService {

    private final ServeMapper serveMapper;
    private final ServeItemMapper serveItemMapper;
    private final RegionMapper regionMapper;

    /**
     * 区域服务分页查询
     * @param servePageQueryReqDTO 区域id
     * @return 分页结果
     */
    @Override
    public PageResult<ServeResDTO> pageQuery(ServePageQueryReqDTO servePageQueryReqDTO) {
        // 方法一
        /*
            Page<Serve> page = PageUtils.parsePageQuery(servePageQueryReqDTO, Serve.class);
            Page<Serve> servePage = baseMapper.selectPage(page, new QueryWrapper<>());
            return PageUtils.toPage(servePage, ServeResDTO.class);
        */
        // 方法二
        return PageHelperUtils.selectPage(servePageQueryReqDTO,
                () -> serveMapper.queryServeListByRegionId(servePageQueryReqDTO.getRegionId()));
    }

    /**
     * 区域服务批量新增
     * @param serveUpsertReqDTOList 服务id、区域id、价格
     */
    @Override
    public void batchAdd(List<ServeUpsertReqDTO> serveUpsertReqDTOList) {
        for (ServeUpsertReqDTO serveUpsertReqDTO : serveUpsertReqDTOList) {
            // 校验服务项是否为启用状态，不是启用状态不能新增
            ServeItem serveItem = serveItemMapper.selectById(serveUpsertReqDTO.getServeItemId());
            if (ObjectUtil.isNull(serveItem) || ObjectUtil.notEqual(serveItem.getActiveStatus(), FoundationStatusEnum.ENABLE.getStatus())){
                throw new ForbiddenOperationException("该服务未启用无法添加到区域下使用");
            }
            // 校验是否重复新增
            LambdaQueryWrapper<Serve> queryWrapper = Wrappers.lambdaQuery(Serve.class)
                    .eq(Serve::getRegionId, serveUpsertReqDTO.getRegionId())
                    .eq(Serve::getServeItemId, serveUpsertReqDTO.getServeItemId());
            List<Serve> serves = baseMapper.selectList(queryWrapper);
            if (CollectionUtil.isNotEmpty(serves)){
                throw new ForbiddenOperationException(serveItem.getName()+"服务已存在");
            }
            // 新增
            Serve serve = BeanUtil.toBean(serveUpsertReqDTO, Serve.class);
            Region region = regionMapper.selectById(serve.getRegionId());
            serve.setCityCode(region.getCityCode());
            baseMapper.insert(serve);
        }
    }

    /**
     * 区域服务价格修改
     * @param id id
     */
    @Override
    public void updatePrice(Long id, BigDecimal price) {
        Serve serve = serveMapper.selectById(id);
        if (ObjectUtil.equals(serve.getSaleStatus(), FoundationStatusEnum.ENABLE.getStatus())){
            throw new ForbiddenOperationException("请先禁用该服务");
        }
        LambdaUpdateWrapper<Serve> updateWrapper = Wrappers.lambdaUpdate(Serve.class)
                .eq(Serve::getId, id);
        serve.setPrice(price);
        int update = baseMapper.update(serve, updateWrapper);
        if (update < 1){
            throw new ForbiddenOperationException("价格修改失败，请稍后重试");
        }
    }

    /**
     * 区域服务上架
     * @param id id
     */
    @Override
    @Transactional
    public void onSale(Long id) {
        /**
         * 区分 服务id和服务项id，
         * 服务id即serve表的主键，
         * 服务项id即serve_item表的主键。
         */
        Serve serve = baseMapper.selectById(id);
        if (ObjectUtil.isNull(serve)){
            throw new ForbiddenOperationException("未找到该服务，请稍后重试");
        }
        Integer saleStatus = serve.getSaleStatus();
        if (ObjectUtil.equals(saleStatus, FoundationStatusEnum.ENABLE.getStatus())){
            throw new ForbiddenOperationException("该服务已经启用");
        }
        Long serveItemId = serve.getServeItemId();
        ServeItem serveItem = serveItemMapper.selectById(serveItemId);
        Integer activeStatus = serveItem.getActiveStatus();
        if (ObjectUtil.notEqual(activeStatus, FoundationStatusEnum.ENABLE.getStatus())){
            throw new ForbiddenOperationException("服务项为启用状态方可上架");
        }
        LambdaUpdateWrapper<Serve> updateWrapper = Wrappers.lambdaUpdate(Serve.class)
                .eq(Serve::getId, id);
        serve.setSaleStatus(FoundationStatusEnum.ENABLE.getStatus());
        int update = baseMapper.update(serve, updateWrapper);
        if (update < 1){
            throw new CommonException("启动服务失败");
        }
    }

    /**
     * 区域服务下架
     * @param id id
     */
    @Override
    @Transactional
    public void offSale(Long id) {
        Serve serve = serveMapper.selectById(id);
        if (ObjectUtil.isNull(serve)){
            throw new ForbiddenOperationException("未找到该服务，请稍后重试");
        }
        if (ObjectUtil.notEqual(serve.getSaleStatus(), FoundationStatusEnum.ENABLE.getStatus())){
            throw new ForbiddenOperationException("请先启用该服务");
        }
        ServeItem serveItem = serveItemMapper.selectById(serve.getServeItemId());
        if (ObjectUtil.notEqual(serveItem.getActiveStatus(), FoundationStatusEnum.ENABLE.getStatus())){
            throw new ForbiddenOperationException("服务项为启用状态方可下架");
        }
        LambdaUpdateWrapper<Serve> updateWrapper = Wrappers.lambdaUpdate(Serve.class)
                .eq(Serve::getId, id);
        serve.setSaleStatus(FoundationStatusEnum.DISABLE.getStatus());
        int update = baseMapper.update(serve, updateWrapper);
        if (update < 1){
            throw new CommonException("下架服务失败");
        }
    }

    /**
     * 区域服务删除
     * @param id id
     */
    @Override
    public void deleteById(Long id) {
        // 判断服务项的状态是否是启用---启用后不能删除---只有状态 为草稿或下架时才可以删除
        Serve serve = baseMapper.selectById(id);
        if (ObjectUtil.isNull(serve)){
            throw new ForbiddenOperationException("未找到该服务，请稍后重试");
        }
        Integer saleStatus = serve.getSaleStatus();
        if (ObjectUtil.equal(saleStatus, FoundationStatusEnum.ENABLE.getStatus())){
            throw new ForbiddenOperationException("该服务启用，请先禁用");
        }
        baseMapper.deleteById(id);
    }
}
