package com.shop.seller.service.impl;

import com.shop.authorization.client.TokensApiClient;
import com.shop.authorization.dto.token.AccessTokenUserInfoDto;
import com.shop.product.client.ProductApiClient;
import com.shop.product.dto.ProductDto;
import com.shop.product.dto.form.product.NewProductForm;
import com.shop.seller.dao.SellerInfoRepository;
import com.shop.seller.dao.SellerProductRepository;
import com.shop.seller.dto.SellerProductDto;
import com.shop.seller.dto.control.CreateProductForm;
import com.shop.seller.dto.control.SellerProductDetailsDto;
import com.shop.seller.model.SellerInfo;
import com.shop.seller.model.SellerProduct;
import com.shop.seller.service.SellerProductsControlService;
import com.shop.seller.service.exception.control.AddNewProductException;
import com.shop.seller.service.exception.control.GetUserInfoApiClientException;
import com.shop.seller.service.exception.control.GetSellerProductsDetailsException;
import com.shop.seller.service.exception.control.RemoveProductFromSellerException;
import com.shop.seller.service.mapper.CreateProductFormMapper;
import com.shop.seller.service.mapper.SellerProductDetailsMapper;
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
public class SellerProductsControlServiceImpl implements SellerProductsControlService {

    private final SellerInfoRepository sellerInfoRepository;
    private final SellerProductRepository sellerProductRepository;
    private final ProductApiClient productApiClient;
    private final TokensApiClient tokensApiClient;
    private final SellerProductDetailsMapper sellerProductDetailsMapper;
    private final CreateProductFormMapper createProductFormMapper;

    @Override
    public List<SellerProductDetailsDto> showAllSellersProducts(Integer page, String accessToken) {
        AccessTokenUserInfoDto userInfo = getUserInfoByAccessTokenFromAuthorizationMicroservice(accessToken);
        try {
            SellerInfo sellerInfo = sellerInfoRepository.getByUserId(userInfo.getUserId());
            List<Long> ids = sellerInfo.getProducts().stream().map(SellerProduct::getProductId).toList();
            List<ProductDto> productDtos = productApiClient.getProductsByIds(page, ids).getBody();

            return mapProductsToProductDetails(sellerInfo, productDtos);
        } catch (Exception e) {
            log.error("Exception while getting sellers products! {}", e.getMessage());
            throw new GetSellerProductsDetailsException(
                    "Exception while getting sellers products! %s".formatted(e.getMessage())
            );
        }
    }

    @Override
    public SellerProductDetailsDto showSellerProduct(Long productId, String accessToken) {
        AccessTokenUserInfoDto userInfo = getUserInfoByAccessTokenFromAuthorizationMicroservice(accessToken);
        try {
            SellerInfo sellerInfo = sellerInfoRepository.getByUserId(userInfo.getUserId());
            SellerProduct sellerProduct = sellerInfo.getProducts().stream()
                    .filter(prod -> prod.getId().equals(productId))
                    .findFirst().orElseThrow(() -> {
                        log.warn("Product with id '{}' not exists or doesn't belong to the seller!", productId);
                        return new GetSellerProductsDetailsException(
                                "Product with id '%s' not exists or doesn't belong to the seller!".formatted(productId)
                        );
                    });
            ProductDto productDto = productApiClient.getProduct(sellerProduct.getProductId()).getBody();

            SellerProductDetailsDto sellerDetails = sellerProductDetailsMapper.mapToDto(sellerProduct);
            sellerProductDetailsMapper.mapProductDto(productDto, sellerDetails);
            return sellerDetails;
        } catch (Exception e) {
            log.error("Exception while getting sellers product with id '{}'! {}", productId, e.getMessage());
            throw new GetSellerProductsDetailsException(
                    "Exception while getting sellers product with id '%s'! %s".formatted(productId, e.getMessage())
            );
        }
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public SellerProductDetailsDto createNewProduct(CreateProductForm form, String accessToken) {
        AccessTokenUserInfoDto userInfo = getUserInfoByAccessTokenFromAuthorizationMicroservice(accessToken);
        try {
            ProductDto product = productApiClient.createProduct(createProductFormMapper.mapToNewProductForm(form)).getBody();
            SellerProduct sellerProduct = saveSellerProductInfo(product);
            SellerInfo sellerInfo = sellerInfoRepository.getByUserId(userInfo.getUserId());
            sellerInfo.getProducts().add(sellerProduct);

            SellerProductDetailsDto productDetails = sellerProductDetailsMapper.mapToDto(sellerProduct);
            sellerProductDetailsMapper.mapProductDto(product, productDetails);
            return productDetails;
        } catch (Exception e) {
            log.error("Unable add a new product to the client! {}", e.getMessage());
            throw new AddNewProductException("Unable add a new product to the client! %s".formatted(e.getMessage()));
        }
    }

    @Override
    public Long removeProduct(Long productId, String accessToken) {
        AccessTokenUserInfoDto userInfo = getUserInfoByAccessTokenFromAuthorizationMicroservice(accessToken);
        try {
            SellerInfo sellerInfo = sellerInfoRepository.getByUserId(userInfo.getUserId());
            SellerProduct sellerProduct = sellerInfo.getProducts().stream()
                    .filter(product -> product.getId().equals(productId))
                    .findFirst()
                    .orElseThrow(() -> {
                        log.error("Product doesn't belong to seller!");
                        return new RemoveProductFromSellerException("Product doesn't belong to seller!");
                    });
            productApiClient.removeProduct(sellerProduct.getProductId());
            sellerProductRepository.deleteById(sellerProduct.getId());
            return productId;
        } catch (Exception e) {
            log.error("Exception while removing product from the client! {}", e.getMessage());
            throw new RemoveProductFromSellerException(
                    "Exception while removing product from the client! %s".formatted(e.getMessage())
            );
        }
    }

    private SellerProduct saveSellerProductInfo(ProductDto product) {
        SellerProduct sellerProduct = new SellerProduct();
        sellerProduct.setProductId(product.getId());
        sellerProduct = sellerProductRepository.save(sellerProduct);
        return sellerProduct;
    }

    private List<SellerProductDetailsDto> mapProductsToProductDetails(SellerInfo sellerInfo, List<ProductDto> productDtos) {
        List<SellerProductDetailsDto> productDetails = sellerInfo.getProducts().stream()
                .map(sellerProductDetailsMapper::mapToDto)
                .toList();
        productDetails.forEach(prodDet ->
            productDtos.stream()
                    .filter(prod -> prodDet.getProductId().equals(prod.getId()))
                    .findFirst()
                    .ifPresent(prod -> sellerProductDetailsMapper.mapProductDto(prod, prodDet))
        );
        return productDetails;
    }

    private AccessTokenUserInfoDto getUserInfoByAccessTokenFromAuthorizationMicroservice(String accessToken) {
        try {
            AccessTokenUserInfoDto userInfo = tokensApiClient.getTokenUserInfo(accessToken).getBody();
            if (userInfo == null) {
                log.error("Returned user info is null!");
                throw new GetUserInfoApiClientException("Returned user info is null!");
            }
            return userInfo;
        } catch (Exception e) {
            log.error("Exception while getting user info from access token! {}", e.getMessage());
            throw new GetUserInfoApiClientException(
                    "Exception while getting user info from access token! %s".formatted(e.getMessage())
            );
        }
    }
}
