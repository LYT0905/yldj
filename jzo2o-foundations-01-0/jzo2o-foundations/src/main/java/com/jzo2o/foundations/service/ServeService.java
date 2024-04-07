package com.jzo2o.foundations.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jzo2o.common.model.PageResult;
import com.jzo2o.foundations.model.domain.Serve;
import com.jzo2o.foundations.model.dto.request.ServePageQueryReqDTO;
import com.jzo2o.foundations.model.dto.request.ServeUpsertReqDTO;
import com.jzo2o.foundations.model.dto.response.ServeResDTO;

import java.util.List;

/**
 * @author LYT0905
 * @date 2024/04/07/20:24
 */

/**
 * 运营端 - 区域服务 Service
 */
public interface ServeService extends IService<Serve> {

    /**
     * 区域服务分页查询
     * @param servePageQueryReqDTO 区域id
     * @return 分页结果
     */
    PageResult<ServeResDTO> pageQuery(ServePageQueryReqDTO servePageQueryReqDTO);

    /**
     * 区域服务批量新增
     * @param serveUpsertReqDTOList 服务id、区域id、价格
     */
    void batchAdd(List<ServeUpsertReqDTO> serveUpsertReqDTOList);

    /**
     * 区域服务上架
     * @param id id
     */
    void onSale(Long id);

    /**
     * 区域服务删除
     * @param id id
     */
    void deleteById(Long id);
}
