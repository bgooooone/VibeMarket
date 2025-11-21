package com.shopping.address.service.impl;

import com.shopping.address.dto.AddressDTO;
import com.shopping.address.entity.Address;
import com.shopping.address.mapper.AddressMapper;
import com.shopping.address.service.AddressService;
import com.shopping.common.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {
    @Autowired
    private AddressMapper addressMapper;

    @Override
    public List<Address> getAddressesByUserId(Long userId) {
        return addressMapper.selectByUserId(userId);
    }

    @Override
    public Address getAddressById(Long id) {
        return addressMapper.selectById(id);
    }

    @Override
    @Transactional
    public Address addAddress(Long userId, AddressDTO addressDTO) {
        Address address = new Address();
        address.setUserId(userId);
        address.setConsignee(addressDTO.getConsignee());
        address.setPhone(addressDTO.getPhone());
        address.setProvince(addressDTO.getProvince());
        address.setCity(addressDTO.getCity());
        address.setDistrict(addressDTO.getDistrict());
        address.setDetailAddress(addressDTO.getDetailAddress());
        
        // 如果设置为默认地址，先清除其他默认地址
        if (addressDTO.getIsDefault() != null && addressDTO.getIsDefault() == 1) {
            addressMapper.clearDefaultByUserId(userId);
            address.setIsDefault(1);
        } else {
            address.setIsDefault(0);
        }
        
        addressMapper.insert(address);
        return address;
    }

    @Override
    @Transactional
    public Address updateAddress(Long userId, Long addressId, AddressDTO addressDTO) {
        Address address = addressMapper.selectById(addressId);
        if (address == null) {
            throw new BusinessException("地址不存在");
        }
        if (!address.getUserId().equals(userId)) {
            throw new BusinessException("无权操作");
        }

        address.setConsignee(addressDTO.getConsignee());
        address.setPhone(addressDTO.getPhone());
        address.setProvince(addressDTO.getProvince());
        address.setCity(addressDTO.getCity());
        address.setDistrict(addressDTO.getDistrict());
        address.setDetailAddress(addressDTO.getDetailAddress());

        // 如果设置为默认地址，先清除其他默认地址
        if (addressDTO.getIsDefault() != null && addressDTO.getIsDefault() == 1) {
            addressMapper.clearDefaultByUserId(userId);
            address.setIsDefault(1);
        } else if (addressDTO.getIsDefault() != null && addressDTO.getIsDefault() == 0) {
            address.setIsDefault(0);
        }

        addressMapper.updateById(address);
        return address;
    }

    @Override
    @Transactional
    public void deleteAddress(Long userId, Long addressId) {
        Address address = addressMapper.selectById(addressId);
        if (address == null) {
            throw new BusinessException("地址不存在");
        }
        if (!address.getUserId().equals(userId)) {
            throw new BusinessException("无权操作");
        }
        addressMapper.deleteById(addressId);
    }

    @Override
    @Transactional
    public Address setDefaultAddress(Long userId, Long addressId) {
        Address address = addressMapper.selectById(addressId);
        if (address == null) {
            throw new BusinessException("地址不存在");
        }
        if (!address.getUserId().equals(userId)) {
            throw new BusinessException("无权操作");
        }

        addressMapper.clearDefaultByUserId(userId);
        address.setIsDefault(1);
        addressMapper.updateById(address);
        return address;
    }
}

