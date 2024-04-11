package com.jzo2o.customer.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jzo2o.api.customer.dto.response.AddressBookResDTO;
import com.jzo2o.common.expcetions.CommonException;
import com.jzo2o.common.expcetions.ForbiddenOperationException;
import com.jzo2o.common.model.PageResult;
import com.jzo2o.common.utils.BeanUtils;
import com.jzo2o.common.utils.CollUtils;
import com.jzo2o.common.utils.StringUtils;
import com.jzo2o.customer.enums.AddressBookIsDeletedStatusEnum;
import com.jzo2o.customer.enums.AddressBookStatusEnum;
import com.jzo2o.customer.mapper.AddressBookMapper;
import com.jzo2o.customer.model.domain.AddressBook;
import com.jzo2o.customer.model.dto.request.AddressBookPageQueryReqDTO;
import com.jzo2o.customer.model.dto.request.AddressBookUpsertReqDTO;
import com.jzo2o.customer.model.dto.response.AddressBookPageQueryRespDTO;
import com.jzo2o.customer.model.dto.response.AddressBookDetailRespDTO;
import com.jzo2o.customer.service.IAddressBookService;
import com.jzo2o.mvc.utils.UserContext;
import com.jzo2o.mysql.utils.PageUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 地址薄 服务实现类
 * </p>
 *
 * @author itcast
 * @since 2023-07-06
 */
@Service
@Resource
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements IAddressBookService {
    @Override
    public List<AddressBookResDTO> getByUserIdAndCity(Long userId, String city) {

        List<AddressBook> addressBooks = lambdaQuery()
                .eq(AddressBook::getUserId, userId)
                .eq(AddressBook::getCity, city)
                .list();
        if(CollUtils.isEmpty(addressBooks)) {
            return new ArrayList<>();
        }
        return BeanUtils.copyToList(addressBooks, AddressBookResDTO.class);
    }

    /**
     * 新增地址簿
     * @param addressBookUpsertReqDTO 请求参数
     */
    @Override
    public void saveAddressBook(AddressBookUpsertReqDTO addressBookUpsertReqDTO) {
        // 经度,纬度
        String location = addressBookUpsertReqDTO.getLocation();
        List<String> split = StringUtils.split(location, ",");
        if (split.size() != 2){
            throw new ForbiddenOperationException("请输入合法地址");
        }
        AddressBook addressBook = new AddressBook()
                .setAddress(addressBookUpsertReqDTO.getAddress())
                .setUserId(UserContext.currentUserId())
                .setCity(addressBookUpsertReqDTO.getCity())
                .setCounty(addressBookUpsertReqDTO.getCounty())
                .setName(addressBookUpsertReqDTO.getName())
                .setLon(Double.valueOf((split.get(0))))
                .setLat(Double.valueOf(split.get(1)))
                .setPhone(addressBookUpsertReqDTO.getPhone())
                .setProvince(addressBookUpsertReqDTO.getProvince())
                .setIsDefault(addressBookUpsertReqDTO.getIsDefault());
        updateExistingDefaultAddressIfNecessary(addressBook);
        baseMapper.insert(addressBook);
    }

    /**
     * 地址薄分页查询
     * @param addressBookPageQueryReqDTO 请求参数
     * @return 返回结果
     */
    @Override
    public PageResult<AddressBookPageQueryRespDTO> pageQuery(AddressBookPageQueryReqDTO addressBookPageQueryReqDTO) {
        Page<AddressBook> addressBookPage = PageUtils.parsePageQuery(addressBookPageQueryReqDTO, AddressBook.class);
        LambdaQueryWrapper<AddressBook> queryWrapper = Wrappers.lambdaQuery(AddressBook.class)
                .eq(AddressBook::getUserId, UserContext.currentUserId())
                .eq(AddressBook::getIsDeleted, AddressBookIsDeletedStatusEnum.IS_NOT_DELETED.getStatus());
        Page<AddressBook> page = baseMapper.selectPage(addressBookPage, queryWrapper);
        return PageUtils.toPage(page, AddressBookPageQueryRespDTO.class);
    }

    /**
     * 地址簿详情
     * @param id 地址簿id
     * @return 响应参数
     */
    @Override
    public AddressBookDetailRespDTO detail(Long id) {
        LambdaQueryWrapper<AddressBook> queryWrapper = Wrappers.lambdaQuery(AddressBook.class)
                .eq(AddressBook::getId, id)
                .eq(AddressBook::getIsDeleted, AddressBookIsDeletedStatusEnum.IS_NOT_DELETED.getStatus());
        AddressBook addressBook = baseMapper.selectOne(queryWrapper);
        if (ObjectUtil.isNull(addressBook)){
            throw new CommonException("查看地址簿失败");
        }
        return BeanUtils.toBean(addressBook, AddressBookDetailRespDTO.class);
    }

    /**
     * 地址簿地址修改
     * @param addressBookUpsertReqDTO 请求参数
     */
    @Override
    public void updateAddressBook(Long id, AddressBookUpsertReqDTO addressBookUpsertReqDTO) {
        LambdaUpdateWrapper<AddressBook> updateWrapper = Wrappers.lambdaUpdate(AddressBook.class)
                .eq(AddressBook::getId, id)
                .eq(AddressBook::getIsDeleted, AddressBookIsDeletedStatusEnum.IS_NOT_DELETED.getStatus());
        AddressBook addressBook = BeanUtils.toBean(addressBookUpsertReqDTO, AddressBook.class);
        updateExistingDefaultAddressIfNecessary(addressBook);
        int update = baseMapper.update(addressBook, updateWrapper);
        if (update < 1){
            throw new CommonException("修改地址失败");
        }
    }

    /**
     * 更新存在默认地址的情况（如果传过来是默认地址，但是之前有默认地址。那么更新之前的默认地址为非默认）
     * @param addressBook 地址
     */
    private void updateExistingDefaultAddressIfNecessary(AddressBook addressBook){
        if (!addressBook.getIsDefault().equals(AddressBookStatusEnum.IS_DEFAULT.getStatus())){
            return;
        }
        LambdaQueryWrapper<AddressBook> queryWrapper = Wrappers.lambdaQuery(AddressBook.class)
                .eq(AddressBook::getUserId, UserContext.currentUserId())
                .eq(AddressBook::getIsDefault, AddressBookStatusEnum.IS_DEFAULT.getStatus());
        AddressBook book = baseMapper.selectOne(queryWrapper);
        if (ObjectUtil.isNotNull(book)){
            book.setIsDefault(AddressBookStatusEnum.IS_NOT_DEFAULT.getStatus());

            LambdaUpdateWrapper<AddressBook> updateWrapper = Wrappers.lambdaUpdate(AddressBook.class)
                    .eq(AddressBook::getId, book.getId());
            baseMapper.update(book, updateWrapper);
        }
    }
}
