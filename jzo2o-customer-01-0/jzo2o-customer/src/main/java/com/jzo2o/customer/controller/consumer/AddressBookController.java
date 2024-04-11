package com.jzo2o.customer.controller.consumer;

/**
 * @author LYT0905
 * @date 2024/04/10/21:57
 */

import com.jzo2o.common.model.PageResult;
import com.jzo2o.customer.model.dto.request.AddressBookPageQueryReqDTO;
import com.jzo2o.customer.model.dto.request.AddressBookUpsertReqDTO;
import com.jzo2o.customer.model.dto.response.AddressBookPageQueryRespDTO;
import com.jzo2o.customer.model.dto.response.AddressBookDetailRespDTO;
import com.jzo2o.customer.service.IAddressBookService;
import com.jzo2o.mvc.model.Result;

import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 地址管理
 */
@RequestMapping("/consumer/address-book")
@RestController("consumerAddressBookController")
public class AddressBookController {
    @Resource
    private IAddressBookService addressBookService;

    /**
     * 新增地址簿
     * @param addressBookUpsertReqDTO 请求参数
     * @return 响应结果
     */
    @PostMapping()
    public Result<String> save(@RequestBody AddressBookUpsertReqDTO addressBookUpsertReqDTO){
        addressBookService.saveAddressBook(addressBookUpsertReqDTO);
        return Result.ok("新增地址成功");
    }

    /**
     * 地址薄分页查询
     * @param addressBookPageQueryReqDTO 请求参数
     * @return 返回结果
     */
    @GetMapping("/page")
    public PageResult<AddressBookPageQueryRespDTO> page(AddressBookPageQueryReqDTO addressBookPageQueryReqDTO){
        return addressBookService.pageQuery(addressBookPageQueryReqDTO);
    }

    /**
     * 地址簿详情
     * @param id 地址簿id
     * @return 响应参数
     */
    @GetMapping("/{id}")
    public AddressBookDetailRespDTO detail(@PathVariable Long id){
        return addressBookService.detail(id);
    }

    /**
     * 地址簿地址修改
     * @param addressBookUpsertReqDTO 请求参数
     * @return 响应参数
     */
    @PutMapping("/{id}")
    public Result<String> update(@PathVariable("id") Long id,@RequestBody AddressBookUpsertReqDTO addressBookUpsertReqDTO){
        addressBookService.updateAddressBook(id, addressBookUpsertReqDTO);
        return Result.ok("修改成功");
    }

    /**
     * 地址簿批量删除
     * @param ids 批量id
     * @return 响应参数
     */
    @DeleteMapping("/batch")
    public Result<String> batchDelete(@RequestBody List<String> ids){
        addressBookService.batchDelete(ids);
        return Result.ok("批量删除成功");
    }

    /**
     * 设置/取消默认地址
     * @param id 地址id
     * @param flag 修改参数（0 非默认地址，1 默认地址）
     * @return 响应参数
     */
    @PutMapping("/default")
    public Result<String> updateDefaultAddress(@RequestParam("id") Long id, @RequestParam("flag") Long flag){
        addressBookService.updateDefaultAddress(id, flag);
        return Result.ok("修改成功");
    }


}
