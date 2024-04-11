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
        AddressBookDetailRespDTO detail = addressBookService.detail(id);
        return detail;
    }
}
