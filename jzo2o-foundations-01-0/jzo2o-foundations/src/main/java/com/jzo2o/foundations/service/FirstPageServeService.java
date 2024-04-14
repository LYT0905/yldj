package com.jzo2o.foundations.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jzo2o.api.foundations.dto.response.ServeItemSimpleResDTO;
import com.jzo2o.api.foundations.dto.response.ServeTypeCategoryResDTO;
import com.jzo2o.foundations.model.dto.request.ServeItemSimpleSearchReqDTO;
import com.jzo2o.foundations.model.dto.response.ServeAggregationSimpleResDTO;
import com.jzo2o.foundations.model.dto.response.ServeAggregationTypeSimpleResDTO;
import com.jzo2o.foundations.model.dto.response.ServeCategoryResDTO;

import java.util.List;

/**
 * @author LYT0905
 * @date 2024/04/14/13:22
 */
public interface FirstPageServeService {
    /**
     * 首页服务列表
     * @param regionId 区域id
     * @return 响应结果
     */
    List<ServeCategoryResDTO> queryServeIconCategoryByRegionIdCache(Long regionId);

    /**
     * 服务类型列表缓存
     * @param regionId 区域id
     * @return 响应结果
     */
    List<ServeAggregationTypeSimpleResDTO> queryServeTypeByRegionId(Long regionId);

    /**
     * 热门服务列表
     * @param regionId 区域id
     * @return 响应结果
     */
    List<ServeAggregationSimpleResDTO> queryHotServeByRegionId(Long regionId);

    /**
     * 服务详情
     * @param id 服务id
     * @return 响应结果
     */
    ServeAggregationSimpleResDTO queryServeDetail(Long id);

}
