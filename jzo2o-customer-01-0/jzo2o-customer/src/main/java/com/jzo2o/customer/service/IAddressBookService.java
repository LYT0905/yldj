package com.jzo2o.customer.service;

import com.jzo2o.api.customer.dto.response.AddressBookResDTO;
import com.jzo2o.common.model.PageResult;
import com.jzo2o.customer.model.domain.AddressBook;
import com.jzo2o.customer.model.dto.request.AddressBookPageQueryReqDTO;
import com.jzo2o.customer.model.dto.request.AddressBookUpsertReqDTO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jzo2o.customer.model.dto.response.AddressBookPageQueryRespDTO;
import com.jzo2o.customer.model.dto.response.AddressBookDetailRespDTO;

import java.util.List;

/**
 * <p>
 * 地址薄 服务类
 * </p>
 *
 * @author itcast
 * @since 2023-07-06
 */
public interface IAddressBookService extends IService<AddressBook> {

    /**
     * 根据用户id和城市编码获取地址
     *
     * @param userId 用户id
     * @param cityCode 城市编码
     * @return 地址编码
     */
    List<AddressBookResDTO> getByUserIdAndCity(Long userId, String cityCode);

    /**
     * 新增地址簿
     * @param addressBookUpsertReqDTO 请求参数
     */
    void saveAddressBook(AddressBookUpsertReqDTO addressBookUpsertReqDTO);

    /**
     * 地址薄分页查询
     * @param addressBookPageQueryReqDTO 请求参数
     * @return 返回结果
     */
    PageResult<AddressBookPageQueryRespDTO> pageQuery(AddressBookPageQueryReqDTO addressBookPageQueryReqDTO);

    /**
     * 地址簿详情
     * @param id 地址簿id
     * @return 响应参数
     */
    AddressBookDetailRespDTO detail(Long id);

    /**
     * 地址簿地址修改
     * @param addressBookUpsertReqDTO 请求参数
     */
    void updateAddressBook(Long id, AddressBookUpsertReqDTO addressBookUpsertReqDTO);

    /**
     * 地址簿批量删除
     * @param ids 批量id
     */
    void batchDelete(List<String> ids);

    /**
     * 设置/取消默认地址
     * @param id 地址id
     * @param flag 修改参数（0 非默认地址，1 默认地址）
     */
    void updateDefaultAddress(Long id, Long flag);
}
