package com.shopping.address.service;

import com.shopping.address.dto.AddressDTO;
import com.shopping.address.entity.Address;

import java.util.List;

public interface AddressService {
    List<Address> getAddressesByUserId(Long userId);
    Address getAddressById(Long id);
    Address addAddress(Long userId, AddressDTO addressDTO);
    Address updateAddress(Long userId, Long addressId, AddressDTO addressDTO);
    void deleteAddress(Long userId, Long addressId);
    Address setDefaultAddress(Long userId, Long addressId);
}

