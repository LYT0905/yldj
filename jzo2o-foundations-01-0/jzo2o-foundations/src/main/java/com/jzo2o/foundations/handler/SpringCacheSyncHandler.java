package com.jzo2o.foundations.handler;

import com.jzo2o.api.foundations.dto.response.RegionSimpleResDTO;
import com.jzo2o.foundations.constants.RedisConstants;
import com.jzo2o.foundations.service.FirstPageServeService;
import com.jzo2o.foundations.service.IRegionService;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * springCache缓存同步任务
 * @author LYT0905
 * @date 2024/04/14/13:14
 */

@Component
@Slf4j
public class SpringCacheSyncHandler {

    @Resource
    private FirstPageServeService firstPageServeService;
    @Resource
    private IRegionService regionService;
    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 已启用区域缓存更新
     * 每日凌晨1点执行
     */
    @XxlJob(value = "activeRegionCacheSync")
    public void activeRegionCacheSync(){
        log.info(">>>>>>>>开始进行缓存同步，更新已启用区域");
        String key =  RedisConstants.CacheName.JZ_CACHE + "::ACTIVE_REGIONS";

        List<RegionSimpleResDTO> regionSimpleResDTOS = regionService.queryActiveRegionList();
        for (RegionSimpleResDTO regionSimpleResDTO : regionSimpleResDTOS) {
            Long regionId = regionSimpleResDTO.getId();//删除该区域下的首页服务列表
            String serve_type_key = RedisConstants.CacheName.SERVE_ICON + "::" + regionId;
            redisTemplate.delete(serve_type_key);
            firstPageServeService.queryServeIconCategoryByRegionIdCache(regionId);
            String serve_type = RedisConstants.CacheName.SERVE_TYPE +"::" + regionId;
            redisTemplate.delete(serve_type);
            firstPageServeService.queryServeTypeByRegionId(regionId);
            String hot_serve_key = RedisConstants.CacheName.HOT_SERVE + "::" + regionId;
            redisTemplate.delete(hot_serve_key);
            firstPageServeService.queryHotServeByRegionId(regionId);
        }

        redisTemplate.delete(key);

        regionService.queryActiveRegionList();
        log.info(">>>>>>>>更新已启用区域完成");
    }
}
