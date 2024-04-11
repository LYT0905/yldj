package com.jzo2o.customer.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jzo2o.api.customer.dto.response.AddressBookResDTO;
import com.jzo2o.api.publics.MapApi;
import com.jzo2o.api.publics.dto.response.LocationResDTO;
import com.jzo2o.common.model.PageResult;
import com.jzo2o.common.utils.BeanUtils;
import com.jzo2o.common.utils.CollUtils;
import com.jzo2o.common.utils.NumberUtils;
import com.jzo2o.common.utils.StringUtils;
import com.jzo2o.customer.enums.AddressBookStatusEnum;
import com.jzo2o.customer.mapper.AddressBookMapper;
import com.jzo2o.customer.model.domain.AddressBook;
import com.jzo2o.customer.model.dto.request.AddressBookPageQueryReqDTO;
import com.jzo2o.customer.model.dto.request.AddressBookUpsertReqDTO;
import com.jzo2o.customer.service.IAddressBookService;
import com.jzo2o.mvc.utils.UserContext;
import com.jzo2o.mysql.utils.PageUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
     * 更新存在默认地址的情况（如果传过来是默认地址，但是之前有默认地址。那么更新之前的默认地址为非默认）
     * @param addressBook
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
