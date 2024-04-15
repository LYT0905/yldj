package com.jzo2o.foundations.controller.consumer;

/**
 * @author LYT0905
 * @date 2024/04/14/13:19
 */

import com.jzo2o.api.foundations.dto.response.ServeItemSimpleResDTO;
import com.jzo2o.api.foundations.dto.response.ServeTypeCategoryResDTO;
import com.jzo2o.foundations.model.domain.ServeType;
import com.jzo2o.foundations.model.dto.request.ServeItemSimpleSearchReqDTO;
import com.jzo2o.foundations.model.dto.response.*;
import com.jzo2o.foundations.service.FirstPageServeService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 门户页
 */
@RestController("firstPageServeController")
@RequestMapping("/customer/serve")
public class FirstPageServeController {

    @Resource
    private FirstPageServeService firstPageServeService;

    /**
     * 首页服务列表
     * @param regionId 区域id
     * @return 响应结果
     */
    @GetMapping("/firstPageServeList")
    public List<ServeCategoryResDTO> serveCategory(@RequestParam("regionId") Long regionId){
        return firstPageServeService.queryServeIconCategoryByRegionIdCache(regionId);
    }

    /**
     * 服务类型列表缓存
     * @param regionId 区域id
     * @return 响应结果
     */
    @GetMapping("/serveTypeList")
    public List<ServeAggregationTypeSimpleResDTO> serveTypeCategory(@RequestParam("regionId") Long regionId){
        return firstPageServeService.queryServeTypeByRegionId(regionId);
    }

    /**
     * 热门服务列表
     * @param regionId 区域id
     * @return 响应结果
     */
    @GetMapping("/hotServeList")
    public List<ServeAggregationSimpleResDTO> hotServe(@RequestParam("regionId") Long regionId){
        return firstPageServeService.queryHotServeByRegionId(regionId);
    }

    /**
     * 服务详情
     * @param id 服务id
     * @return 响应结果
     */
    @GetMapping("/{id}")
    public ServeAggregationSimpleResDTO serveDetail(@PathVariable Long id){
        return firstPageServeService.queryServeDetail(id);
    }

    /**
     * 首页服务搜索
     * @param cityCode 城市编码
     * @param serveTypeId 服务类型
     * @param keyword 关键词
     * @return 响应参数
     */
    @GetMapping("/search")
    public List<ServeSimpleResDTO> findServeList(@RequestParam("cityCode") String cityCode,
                                                 @RequestParam(value = "serveTypeId", required = false) Long serveTypeId,
                                                 @RequestParam(value = "keyword", required = false) String keyword) {
        return firstPageServeService.findServeList(cityCode, serveTypeId, keyword);
    }
}
