package com.shopping.cart.service.impl;

import com.shopping.cart.dto.CartDTO;
import com.shopping.cart.entity.Cart;
import com.shopping.cart.mapper.CartMapper;
import com.shopping.cart.service.CartService;
import com.shopping.common.exception.BusinessException;
import com.shopping.product.entity.Product;
import com.shopping.product.mapper.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private ProductMapper productMapper;

    @Override
    public List<Cart> getCartList(Long userId) {
        return cartMapper.selectByUserId(userId);
    }

    @Override
    @Transactional
    public Cart addToCart(Long userId, CartDTO cartDTO) {
        Product product = productMapper.selectById(cartDTO.getProductId());
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        if (product.getStatus() != 1) {
            throw new BusinessException("商品已下架");
        }
        if (product.getStock() < cartDTO.getQuantity()) {
            throw new BusinessException("库存不足");
        }

        Cart existingCart = cartMapper.selectByUserIdAndProductId(userId, cartDTO.getProductId());
        if (existingCart != null) {
            int newQuantity = existingCart.getQuantity() + cartDTO.getQuantity();
            if (newQuantity > product.getStock()) {
                throw new BusinessException("库存不足");
            }
            existingCart.setQuantity(newQuantity);
            cartMapper.updateById(existingCart);
            return existingCart;
        } else {
            Cart cart = new Cart();
            cart.setUserId(userId);
            cart.setProductId(cartDTO.getProductId());
            cart.setQuantity(cartDTO.getQuantity());
            cart.setSelected(cartDTO.getSelected() != null ? cartDTO.getSelected() : 1);
            cartMapper.insert(cart);
            return cart;
        }
    }

    @Override
    @Transactional
    public Cart updateCart(Long userId, Long cartId, CartDTO cartDTO) {
        Cart cart = cartMapper.selectById(cartId);
        if (cart == null) {
            throw new BusinessException("购物车项不存在");
        }
        if (!cart.getUserId().equals(userId)) {
            throw new BusinessException("无权操作");
        }

        Product product = productMapper.selectById(cart.getProductId());
        if (product == null || product.getStatus() != 1) {
            throw new BusinessException("商品已下架");
        }
        if (product.getStock() < cartDTO.getQuantity()) {
            throw new BusinessException("库存不足");
        }

        cart.setQuantity(cartDTO.getQuantity());
        if (cartDTO.getSelected() != null) {
            cart.setSelected(cartDTO.getSelected());
        }
        cartMapper.updateById(cart);
        return cart;
    }

    @Override
    @Transactional
    public void removeFromCart(Long userId, Long cartId) {
        Cart cart = cartMapper.selectById(cartId);
        if (cart == null) {
            throw new BusinessException("购物车项不存在");
        }
        if (!cart.getUserId().equals(userId)) {
            throw new BusinessException("无权操作");
        }
        cartMapper.deleteById(cartId);
    }

    @Override
    @Transactional
    public void clearCart(Long userId) {
        cartMapper.deleteByUserId(userId);
    }

    @Override
    @Transactional
    public void selectCartItem(Long userId, Long cartId, Integer selected) {
        Cart cart = cartMapper.selectById(cartId);
        if (cart == null) {
            throw new BusinessException("购物车项不存在");
        }
        if (!cart.getUserId().equals(userId)) {
            throw new BusinessException("无权操作");
        }
        cart.setSelected(selected);
        cartMapper.updateById(cart);
    }
}

