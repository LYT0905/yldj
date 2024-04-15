package com.jzo2o.foundations.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.jzo2o.api.foundations.dto.response.ServeItemSimpleResDTO;
import com.jzo2o.api.foundations.dto.response.ServeTypeCategoryResDTO;
import com.jzo2o.common.expcetions.ForbiddenOperationException;
import com.jzo2o.common.utils.ObjectUtils;
import com.jzo2o.es.core.ElasticSearchTemplate;
import com.jzo2o.es.utils.SearchResponseUtils;
import com.jzo2o.foundations.constants.RedisConstants;
import com.jzo2o.foundations.enums.FoundationStatusEnum;
import com.jzo2o.foundations.mapper.ServeMapper;
import com.jzo2o.foundations.model.domain.Region;
import com.jzo2o.foundations.model.domain.Serve;
import com.jzo2o.foundations.model.domain.ServeAggregation;
import com.jzo2o.foundations.model.dto.request.ServeItemSimpleSearchReqDTO;
import com.jzo2o.foundations.model.dto.response.*;
import com.jzo2o.foundations.service.FirstPageServeService;
import com.jzo2o.foundations.service.IRegionService;
import com.jzo2o.foundations.service.ServeService;
import org.apache.tomcat.util.security.Escape;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author LYT0905
 * @date 2024/04/14/13:23
 */

@Service
public class FirstPageServeServiceImpl implements FirstPageServeService {

    @Resource
    private IRegionService regionService;
    @Resource
    private ServeMapper serveMapper;
    @Resource
    private ElasticSearchTemplate elasticSearchTemplate;

    /**
     * 首页服务列表
     * @param regionId 区域id
     * @return 响应结果
     */
    @Override
    @Caching(
            cacheable = {
                    //result为null时,属于缓存穿透情况，缓存时间30分钟
                    @Cacheable(value = RedisConstants.CacheName.SERVE_ICON, key = "#regionId", unless = "#result.size() != 0", cacheManager = RedisConstants.CacheManager.THIRTY_MINUTES),
                    //result不为null时,永久缓存
                    @Cacheable(value = RedisConstants.CacheName.SERVE_ICON, key = "#regionId", unless = "#result.size() == 0", cacheManager = RedisConstants.CacheManager.FOREVER)
            }
    )
    public List<ServeCategoryResDTO> queryServeIconCategoryByRegionIdCache(Long regionId) {
        Region region = regionService.getById(regionId);
        if (ObjectUtil.notEqual(region.getActiveStatus(), FoundationStatusEnum.ENABLE.getStatus()) || ObjectUtil.isEmpty(region)){
            throw new ForbiddenOperationException("请求出错");
        }
        List<ServeCategoryResDTO> list = serveMapper.findServeIconCategoryByRegionId(regionId);
        if (CollUtil.isEmpty(list)){
            throw new ForbiddenOperationException("未查找到相关服务");
        }
        //3.服务类型取前两个，每个类型下服务项取前4个
        //list的截止下标
        int endIndex = list.size() >= 2 ? 2 : list.size();
        List<ServeCategoryResDTO> serveCategoryResDTOS = new ArrayList<>(list.subList(0, endIndex));
        serveCategoryResDTOS.forEach(v -> {
            List<ServeSimpleResDTO> serveResDTOList = v.getServeResDTOList();
            //serveResDTOList的截止下标
            int endIndex2 = serveResDTOList.size() >= 4 ? 4 : serveResDTOList.size();
            List<ServeSimpleResDTO> serveSimpleResDTOS = new ArrayList<>(serveResDTOList.subList(0, endIndex2));
            v.setServeResDTOList(serveSimpleResDTOS);
        });
        return serveCategoryResDTOS;
    }

    /**
     * 服务类型列表缓存
     * @param regionId 区域id
     * @return 响应结果
     */
    @Override
    @Caching(cacheable = {
            @Cacheable(value = RedisConstants.CacheName.SERVE_TYPE, key = "#regionId", unless = "#result.size() == 0", cacheManager = RedisConstants.CacheManager.FOREVER),
            @Cacheable(value = RedisConstants.CacheName.SERVE_TYPE, key = "#regionId", unless = "#result.size() != 0", cacheManager = RedisConstants.CacheManager.THIRTY_MINUTES)
    })
    public List<ServeAggregationTypeSimpleResDTO> queryServeTypeByRegionId(Long regionId) {
        Region region = regionService.getById(regionId);
        if (ObjectUtil.notEqual(region.getActiveStatus(), FoundationStatusEnum.ENABLE.getStatus()) || ObjectUtil.isEmpty(region)){
            throw new ForbiddenOperationException("请求出错");
        }
        List<ServeAggregationTypeSimpleResDTO> list = serveMapper.serveTypeCategoryByRegionId(regionId);
        if (CollUtil.isEmpty(list)){
            throw new ForbiddenOperationException("未查找到相关服务");
        }
        return list;
    }

    /**
     * 热门服务列表
     * @param regionId 区域id
     * @return 响应结果
     */
    @Override
    public List<ServeAggregationSimpleResDTO> queryHotServeByRegionId(Long regionId) {
        Region region = regionService.getById(regionId);
        if (ObjectUtil.notEqual(region.getActiveStatus(), FoundationStatusEnum.ENABLE.getStatus()) || ObjectUtil.isEmpty(region)){
            throw new ForbiddenOperationException("请求出错");
        }
        List<ServeAggregationSimpleResDTO> list = serveMapper.queryHotServeByRegionId(regionId);
        if (CollUtil.isEmpty(list)){
            throw new ForbiddenOperationException("未查找到相关服务");
        }
        return list;
    }

    /**
     * 服务详情
     * @param id 服务id
     * @return 响应结果
     */
    @Override
    public ServeAggregationSimpleResDTO queryServeDetail(Long id) {
        return serveMapper.queryServeDetail(id);
    }

    /**
     * 首页服务搜索
     * @param cityCode 城市编码
     * @param serveTypeId 服务类型
     * @param keyword 关键词
     * @return 响应参数
     */
    @Override
    public List<ServeSimpleResDTO> findServeList(String cityCode, Long serveTypeId, String keyword) {
        // 构造查询条件
        SearchRequest.Builder builder = new SearchRequest.Builder();

        builder.query(query->query.bool(bool->{

            //匹配citycode
            bool.must(must->
                    must.term(term->
                            term.field("city_code").value(cityCode)));

            //todo 匹配服务类型
            if (ObjectUtil.isNotNull(serveTypeId)){
                bool.must(must ->
                        must.term(term ->
                                term.field("serve_type_id").value(serveTypeId)));
            }
            //匹配关键字
            if(ObjectUtils.isNotEmpty(keyword)){
                bool.must(must->
                        must.multiMatch(multiMatch->
                                multiMatch.fields("serve_item_name","serve_type_name").query(keyword)));
            }
            return bool;
        }));
        // 排序 按服务项的serveItemSortNum排序(升序)
        List<SortOptions> sortOptions = new ArrayList<>();
        sortOptions.add(SortOptions.of(sortOption -> sortOption.field(field->field.field("serve_item_sort_num").order(SortOrder.Asc))));
        builder.sort(sortOptions);
        //指定索引
        builder.index("serve_aggregation");
        //请求对象
        SearchRequest searchRequest = builder.build();
        // 检索数据
        SearchResponse<ServeAggregation> searchResponse = elasticSearchTemplate.opsForDoc().search(searchRequest, ServeAggregation.class);
        //如果搜索成功返回结果集
        if (SearchResponseUtils.isSuccess(searchResponse)) {
            List<ServeAggregation> collect = searchResponse.hits().hits()
                    .stream().map(hit -> {
                        ServeAggregation serve = hit.source();
                        return serve;
                    })
                    .collect(Collectors.toList());
            List<ServeSimpleResDTO> serveSimpleResDTOS = BeanUtil.copyToList(collect, ServeSimpleResDTO.class);
            return serveSimpleResDTOS;
        }
        return  Collections.emptyList();
    }
}
