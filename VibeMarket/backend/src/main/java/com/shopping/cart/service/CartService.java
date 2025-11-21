package com.shopping.cart.service;

import com.shopping.cart.dto.CartDTO;
import com.shopping.cart.entity.Cart;

import java.util.List;

public interface CartService {
    List<Cart> getCartList(Long userId);
    Cart addToCart(Long userId, CartDTO cartDTO);
    Cart updateCart(Long userId, Long cartId, CartDTO cartDTO);
    void removeFromCart(Long userId, Long cartId);
    void clearCart(Long userId);
    void selectCartItem(Long userId, Long cartId, Integer selected);
}

