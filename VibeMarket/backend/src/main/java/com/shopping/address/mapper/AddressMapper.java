package com.shopping.address.mapper;

import com.shopping.address.entity.Address;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AddressMapper {
    Address selectById(Long id);
    List<Address> selectByUserId(Long userId);
    Address selectDefaultByUserId(Long userId);
    int insert(Address address);
    int updateById(Address address);
    int deleteById(Long id);
    int clearDefaultByUserId(Long userId);
}

