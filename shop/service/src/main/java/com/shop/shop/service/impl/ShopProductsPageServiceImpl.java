package com.shop.shop.service.impl;

import com.shop.authorization.client.TokensApiClient;
import com.shop.authorization.dto.token.AccessTokenUserInfoDto;
import com.shop.common.utils.all.consts.ApprovalStatuses;
import com.shop.common.utils.all.exception.dao.CommonRepositoryException;
import com.shop.product.client.ProductApiClient;
import com.shop.product.dto.ProductDto;
import com.shop.product.dto.form.product.ProductFilterForm;
import com.shop.shop.dao.CartItemRepository;
import com.shop.shop.dao.UserCartRepository;
import com.shop.shop.dto.UserCartDto;
import com.shop.shop.dto.form.shop.AddProductToCartForm;
import com.shop.shop.dto.form.shop.RemoveProductFromCartForm;
import com.shop.shop.dto.shop.ShopPageProductInfoDto;
import com.shop.shop.model.CartItem;
import com.shop.shop.model.UserCart;
import com.shop.shop.service.ShopProductsPageService;
import com.shop.shop.service.exception.shop.*;
import com.shop.shop.service.mapper.UserCartMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShopProductsPageServiceImpl implements ShopProductsPageService {

    private final UserCartRepository userCartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductApiClient productApiClient;
    private final TokensApiClient tokensApiClient;
    private final UserCartMapper userCartMapper;

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<ShopPageProductInfoDto> showFilteredProductsPage(Integer page, @Valid ProductFilterForm form) {
        List<ProductDto> products = getProductsFromClient(page, form);
        checkIfFoundProductsNotNull(products);

        return products.stream()
                .map(ShopPageProductInfoDto::new)
                .toList();
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public UserCartDto addProductToCart(AddProductToCartForm form) {
        AccessTokenUserInfoDto userInfo = tokensApiClient.getTokenUserInfo(form.getUserAccessToken()).getBody();
        checkIfUserInfoIsNull(userInfo);

        UserCart userCart = getOrCreateUserCart(userInfo);

        ProductDto productDto = productApiClient.getProduct(form.getProductId()).getBody();
        checkIfProductIsNull(productDto);

        checkProductCount(form, productDto);

        CartItem cartItem = saveCartItem(userCart, form, productDto);

        // Is it possible make it more beautiful?
        if (userCart.getCartItems() == null) userCart.setCartItems(new ArrayList<>());
        userCart.getCartItems().add(cartItem);

        return userCartMapper.mapToDto(userCart);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public UserCartDto removeProductFromCart(RemoveProductFromCartForm form) {
        AccessTokenUserInfoDto userInfo = tokensApiClient.getTokenUserInfo(form.getAccessToken()).getBody();
        checkIfUserInfoIsNull(userInfo);

        UserCart userCart = getOrCreateUserCart(userInfo);
        userCart.getCartItems().removeIf(item -> item.getId().equals(form.getProductId()));

        return userCartMapper.mapToDto(userCart);
    }

    @Override
    public ShopPageProductInfoDto showProductInfo(Long productId) {
        ProductDto productDto = productApiClient.getProduct(productId).getBody();
        checkIfProductIsNull(productDto);

        checkIfProductApproved(productId, productDto);

        return new ShopPageProductInfoDto(productDto);
    }

    private static void checkIfProductApproved(Long productId, ProductDto productDto) {
        if (!productDto.getApprovalStatus().equals(ApprovalStatuses.APPROVED)) {
            log.warn("Unable to show unapproved product! Product with id '{}' unapproved!", productId);
            throw new ProductForbiddenException(
                    "Unable to show unapproved product! Product with id '%s' unapproved!".formatted(productId)
            );
        }
    }

    private CartItem saveCartItem(UserCart userCart, AddProductToCartForm form, ProductDto productDto) {
        CartItem cartItem = new CartItem();
        cartItem.setUserCart(userCart);
        cartItem.setProductId(productDto.getId());
        cartItem.setCount(form.getCount());
        cartItem.setAdditionTime(LocalDateTime.now());
        return cartItemRepository.save(cartItem);
    }

    private void checkProductCount(AddProductToCartForm form, ProductDto productDto) {
        if (productDto.getCount() < form.getCount()) {
            log.warn("Unable add '{}' products with id '{}' to the cart! Product's count less than adding value!",
                    form.getCount(), productDto.getId());
            throw new NotEnoughProductsException(
                    "Unable add '%S' products with id '%s' to the cart! Product's count less than adding value!"
                            .formatted(form.getCount(), productDto.getId())
            );
        }
    }

    private void checkIfProductIsNull(ProductDto productDto) {
        if (productDto == null) {
            log.error("Unable add a new product to cart! Products client return null product!");
            throw new NullProductClientException("Unable add a new product to cart! Products client return null product!");
        }
    }

    private UserCart getOrCreateUserCart(AccessTokenUserInfoDto userInfo) {
        try {
            if (userCartRepository.existsByUserId(userInfo.getUserId())) {
                return userCartRepository.getReferenceByUserId(userInfo.getUserId());
            } else {
                UserCart userCart = new UserCart();
                userCart.setUserId(userInfo.getUserId());
                userCart.setUserLogin(userInfo.getUserLogin());
                return userCartRepository.save(userCart);
            }
        } catch (Exception e) {
            log.error("Exception while getting or creating a new user cart! {}", e.getMessage());
            throw new CommonRepositoryException(
                    "Exception while getting or creating a new user cart! %s".formatted(e.getMessage())
            );
        }
    }

    private void checkIfUserInfoIsNull(AccessTokenUserInfoDto userInfo) {
        if (userInfo == null) {
            log.error("Token api client return null user info for th access token!");
            throw new NullUserInfoTokenApiClientException("Token api client return null user info for th access token!");
        }
    }

    private void checkIfFoundProductsNotNull(List<ProductDto> products) {
        if (products == null || products.isEmpty()) {
            log.warn("No one product was found for a such filter request!");
            throw new ProductsNotFoundForFilterException("No one product was found for a such filter request!");
        }
    }

    private List<ProductDto> getProductsFromClient(Integer page, ProductFilterForm form) {
        try {
            return productApiClient.getFilteredApprovedProducts(page, form).getBody();
        } catch (Exception e) {
            log.error("Exception while getting products from client! {}", e.getMessage());
            throw new GetProductsClientException(
                    "Exception while getting products from client! %s".formatted(e.getMessage())
            );
        }
    }
}
