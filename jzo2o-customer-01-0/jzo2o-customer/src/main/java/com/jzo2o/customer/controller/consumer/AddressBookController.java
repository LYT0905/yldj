package com.jzo2o.customer.controller.consumer;

/**
 * @author LYT0905
 * @date 2024/04/10/21:57
 */

import com.jzo2o.customer.model.dto.request.AddressBookUpsertReqDTO;
import com.jzo2o.customer.service.IAddressBookService;
import com.jzo2o.mvc.model.Result;
import org.apache.tomcat.jni.Address;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public Result<Object> save(@RequestBody AddressBookUpsertReqDTO addressBookUpsertReqDTO){
        addressBookService.saveAddressBook(addressBookUpsertReqDTO);
        return Result.ok("新增地址成功");
    }
}
