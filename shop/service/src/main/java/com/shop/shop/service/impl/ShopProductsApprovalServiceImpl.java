package com.shop.shop.service.impl;

import com.shop.product.client.ProductApiClient;
import com.shop.product.dto.ProductDto;
import com.shop.product.dto.form.product.ApprovalStatusProductFilterForm;
import com.shop.product.dto.form.product.ChangeProductDataForm;
import com.shop.shop.dto.shop.ShopPageProductInfoDto;
import com.shop.shop.service.ShopProductsApprovalService;
import com.shop.shop.service.exception.shop.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShopProductsApprovalServiceImpl implements ShopProductsApprovalService {

    private final ProductApiClient productApiClient;

    @Override
    public List<ShopPageProductInfoDto> showProductsPage(Integer page, ApprovalStatusProductFilterForm form) {
        List<ProductDto> products = getProductsFromClient(page, form);
        checkIfFoundProductsNotNull(products);

        return products.stream()
                .map(ShopPageProductInfoDto::new)
                .toList();
    }

    @Override
    public ShopPageProductInfoDto showProductInfo(Long id) {
        ProductDto product = getProductFromClient(id);
        checkIfFoundProductNotNull(product);

        return new ShopPageProductInfoDto(product);
    }

    @Override
    public ShopPageProductInfoDto changeProductInfo(ChangeProductDataForm form) {
        ProductDto productDto = changeProductData(form);
        checkIfFoundProductNotNull(productDto);

        return new ShopPageProductInfoDto(productDto);
    }

    private ProductDto changeProductData(ChangeProductDataForm form) {
        try {
            return productApiClient.updateProductData(form).getBody();
        } catch (Exception e) {
            log.error("Exception while updating product from the client! {}", e.getMessage());
            throw new ChangeProductDataException(
                    "Exception while updating product from the client! %s".formatted(e.getMessage())
            );
        }
    }

    private void checkIfFoundProductNotNull(ProductDto productDto) {
        if (productDto == null) {
            log.warn("Client was return null product!");
            throw new ProductNotFoundException("Client was return null product!");
        }
    }

    private void checkIfFoundProductsNotNull(List<ProductDto> products) {
        if (products == null || products.isEmpty()) {
            log.warn("No one product was found for a such filter request!");
            throw new ProductsNotFoundForFilterException("No one product was found for a such filter request!");
        }
    }

    private ProductDto getProductFromClient(Long id) {
        try {
            return productApiClient.getProduct(id).getBody();
        } catch (Exception e) {
            log.error("Exception while getting product from client! {}", e.getMessage());
            throw new GetProductClientException(
                    "Exception while getting product from client! %s".formatted(e.getMessage())
            );
        }
    }

    private List<ProductDto> getProductsFromClient(Integer page, ApprovalStatusProductFilterForm form) {
        try {
            return productApiClient.getFilteredApprovalStatusProducts(page, form).getBody();
        } catch (Exception e) {
            log.error("Exception while getting products from client! {}", e.getMessage());
            throw new GetProductsClientException(
                    "Exception while getting products from client! %s".formatted(e.getMessage())
            );
        }
    }
}
