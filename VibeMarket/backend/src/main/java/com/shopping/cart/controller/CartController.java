package com.shopping.cart.controller;

import com.shopping.common.annotation.CurrentUser;
import com.shopping.common.response.ResponseResult;
import com.shopping.cart.dto.CartDTO;
import com.shopping.cart.entity.Cart;
import com.shopping.cart.service.CartService;
import com.shopping.user.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cart")
public class CartController {
    @Autowired
    private CartService cartService;

    @GetMapping("/list")
    public ResponseResult<List<Cart>> getCartList(@CurrentUser User currentUser) {
        List<Cart> cartList = cartService.getCartList(currentUser.getId());
        return ResponseResult.success(cartList);
    }

    @PostMapping("/add")
    public ResponseResult<Cart> addToCart(@CurrentUser User currentUser, 
                                         @Validated @RequestBody CartDTO cartDTO) {
        Cart cart = cartService.addToCart(currentUser.getId(), cartDTO);
        return ResponseResult.success("添加成功", cart);
    }

    @PutMapping("/{cartId}")
    public ResponseResult<Cart> updateCart(@CurrentUser User currentUser,
                                           @PathVariable Long cartId,
                                           @Validated @RequestBody CartDTO cartDTO) {
        Cart cart = cartService.updateCart(currentUser.getId(), cartId, cartDTO);
        return ResponseResult.success("更新成功", cart);
    }

    @DeleteMapping("/{cartId}")
    public ResponseResult<String> removeFromCart(@CurrentUser User currentUser,
                                                  @PathVariable Long cartId) {
        cartService.removeFromCart(currentUser.getId(), cartId);
        return ResponseResult.success("删除成功");
    }

    @DeleteMapping("/clear")
    public ResponseResult<String> clearCart(@CurrentUser User currentUser) {
        cartService.clearCart(currentUser.getId());
        return ResponseResult.success("清空成功");
    }

    @PutMapping("/{cartId}/select")
    public ResponseResult<String> selectCartItem(@CurrentUser User currentUser,
                                                  @PathVariable Long cartId,
                                                  @RequestParam Integer selected) {
        cartService.selectCartItem(currentUser.getId(), cartId, selected);
        return ResponseResult.success("操作成功");
    }
}

