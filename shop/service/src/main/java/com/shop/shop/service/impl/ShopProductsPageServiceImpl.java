package com.shop.shop.service.impl;

import com.shop.product.client.ProductApiClient;
import com.shop.product.dto.ProductDto;
import com.shop.product.dto.form.product.ProductFilterForm;
import com.shop.shop.dto.shop.ShopPageProductInfoDto;
import com.shop.shop.service.ShopProductsPageService;
import com.shop.shop.service.exception.shop.GetProductsClientException;
import com.shop.shop.service.exception.shop.ProductsNotFoundForFilterException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShopProductsPageServiceImpl implements ShopProductsPageService {

    private final ProductApiClient productApiClient;

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<ShopPageProductInfoDto> showFilteredProductsPage(Integer page, @Valid ProductFilterForm form) {
        List<ProductDto> products = getProductsFromClient(page, form);
        checkIfFoundProductsNotNull(products);

        return products.stream()
                .map(ShopPageProductInfoDto::new)
                .toList();
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
