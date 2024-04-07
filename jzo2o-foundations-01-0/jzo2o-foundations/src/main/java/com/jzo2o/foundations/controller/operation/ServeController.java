package com.jzo2o.foundations.controller.operation;

import com.jzo2o.common.model.PageResult;
import com.jzo2o.foundations.model.domain.Serve;
import com.jzo2o.foundations.model.dto.request.ServePageQueryReqDTO;
import com.jzo2o.foundations.model.dto.request.ServeUpsertReqDTO;
import com.jzo2o.foundations.model.dto.response.ServeResDTO;
import com.jzo2o.foundations.service.ServeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author LYT0905
 * @date 2024/04/07/20:13
 */

@RestController("operationServerController")
@RequestMapping("/operation/serve")
@Api(tags = "运营端 - 区域服务相关接口")
public class ServeController {

    @Resource
    private ServeService serveService;

    /**
     * 区域服务分页查询
     * @param servePageQueryReqDTO 区域id
     * @return 分页结果
     */
    @GetMapping("/page")
    @ApiOperation("区域服务分页查询")
    public PageResult<ServeResDTO> page(ServePageQueryReqDTO servePageQueryReqDTO){
        return serveService.pageQuery(servePageQueryReqDTO);
    }

    /**
     * 区域服务批量新增
     * @param serveUpsertReqDTOList 服务id、区域id、价格
     */
    @PostMapping("/batch")
    @ApiOperation("区域服务批量新增")
    public void save(@RequestBody List<ServeUpsertReqDTO> serveUpsertReqDTOList){
        serveService.batchAdd(serveUpsertReqDTOList);
    }

    /**
     * 区域服务上架
     * @param id id
     */
    @PutMapping("/onSale/{id}")
    public void onSale(@PathVariable Long id){
        serveService.onSale(id);
    }

    /**
     * 区域服务删除
     * @param id id
     */
    @DeleteMapping("/{id}")
    @ApiOperation("区域服务删除")
    public void delete(@PathVariable Long id){
        serveService.deleteById(id);
    }
}
