package com.shopping.address.controller;

import com.shopping.address.dto.AddressDTO;
import com.shopping.address.entity.Address;
import com.shopping.address.service.AddressService;
import com.shopping.common.annotation.CurrentUser;
import com.shopping.common.response.ResponseResult;
import com.shopping.user.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/addresses")
public class AddressController {
    @Autowired
    private AddressService addressService;

    @GetMapping("/list")
    public ResponseResult<List<Address>> getAddresses(@CurrentUser User currentUser) {
        List<Address> addresses = addressService.getAddressesByUserId(currentUser.getId());
        return ResponseResult.success(addresses);
    }

    @GetMapping("/{id}")
    public ResponseResult<Address> getAddress(@CurrentUser User currentUser, @PathVariable Long id) {
        Address address = addressService.getAddressById(id);
        if (address == null) {
            return ResponseResult.error("地址不存在");
        }
        if (!address.getUserId().equals(currentUser.getId())) {
            return ResponseResult.error("无权访问");
        }
        return ResponseResult.success(address);
    }
    //增添地址
    @PostMapping("/add")
    public ResponseResult<Address> addAddress(@CurrentUser User currentUser,
                                            @Validated @RequestBody AddressDTO addressDTO) {
        Address address = addressService.addAddress(currentUser.getId(), addressDTO);
        return ResponseResult.success("添加成功", address);
    }
    //更新地址
    @PutMapping("/{id}")
    public ResponseResult<Address> updateAddress(@CurrentUser User currentUser,
                                                @PathVariable Long id,
                                                @Validated @RequestBody AddressDTO addressDTO) {
        Address address = addressService.updateAddress(currentUser.getId(), id, addressDTO);
        return ResponseResult.success("更新成功", address);
    }
    //删除地址
    @DeleteMapping("/{id}")
    public ResponseResult<String> deleteAddress(@CurrentUser User currentUser, @PathVariable Long id) {
        addressService.deleteAddress(currentUser.getId(), id);
        return ResponseResult.success("删除成功");
    }

    @PutMapping("/{id}/default")
    public ResponseResult<Address> setDefaultAddress(@CurrentUser User currentUser, @PathVariable Long id) {
        Address address = addressService.setDefaultAddress(currentUser.getId(), id);
        return ResponseResult.success("设置成功", address);
    }
}

