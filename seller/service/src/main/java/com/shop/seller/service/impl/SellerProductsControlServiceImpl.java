package com.shop.seller.service.impl;

import com.shop.authorization.client.TokensApiClient;
import com.shop.authorization.dto.token.AccessTokenUserInfoDto;
import com.shop.common.utils.all.exception.dao.EntityNotFoundRepositoryException;
import com.shop.media.client.ProductMediaApiClient;
import com.shop.media.common.data.builder.CreateMediaForProductFormBuilder;
import com.shop.media.dto.ProductMediaDto;
import com.shop.media.dto.form.AddMediaToProductForm;
import com.shop.media.dto.metadata.DockMetadataDto;
import com.shop.media.dto.metadata.ImgMetadataDto;
import com.shop.product.client.ProductApiClient;
import com.shop.product.client.ProductCategoryApiClient;
import com.shop.product.client.ProductDiscountApiClient;
import com.shop.product.dto.CategoryDto;
import com.shop.product.dto.DiscountDto;
import com.shop.product.dto.ProductDto;
import com.shop.seller.dao.SellerInfoRepository;
import com.shop.seller.dao.SellerProductRepository;
import com.shop.seller.dto.control.CreateProductForm;
import com.shop.seller.dto.control.SellerProductDetailsDto;
import com.shop.seller.dto.control.UpdateSellerProductForm;
import com.shop.seller.model.SellerInfo;
import com.shop.seller.model.SellerProduct;
import com.shop.seller.service.SellerProductsControlService;
import com.shop.seller.service.exception.control.*;
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
    private final ProductCategoryApiClient productCategoryApiClient;
    private final ProductDiscountApiClient productDiscountApiClient;
    private final TokensApiClient tokensApiClient;
    private final SellerProductDetailsMapper sellerProductDetailsMapper;
    private final CreateProductFormMapper createProductFormMapper;
    private final ProductMediaApiClient productMediaApiClient;

    @Override
    public List<SellerProductDetailsDto> showAllSellersProducts(Integer page, String accessToken) {
        AccessTokenUserInfoDto userInfo = getUserInfoByAccessToken(accessToken);
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
        AccessTokenUserInfoDto userInfo = getUserInfoByAccessToken(accessToken);
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
            sellerProductDetailsMapper.mapSellerProductDetailsDto(productDto, sellerDetails);
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
        AccessTokenUserInfoDto userInfo = getUserInfoByAccessToken(accessToken);
        try {
            ProductDto product = createProductAndMedia(form);
            SellerProduct sellerProduct = saveSellerProductInfo(product);
            SellerInfo sellerInfo = sellerInfoRepository.getByUserId(userInfo.getUserId());
            sellerInfo.getProducts().add(sellerProduct);

            SellerProductDetailsDto productDetails = sellerProductDetailsMapper.mapToDto(sellerProduct);
            sellerProductDetailsMapper.mapSellerProductDetailsDto(product, productDetails);
            return productDetails;
        } catch (Exception e) {
            log.error("Unable add a new product to the client! {}", e.getMessage());
            throw new AddNewProductException("Unable add a new product to the client! %s".formatted(e.getMessage()));
        }
    }

    private ProductDto createProductAndMedia(CreateProductForm form) {
        ProductDto product = productApiClient.createProduct(createProductFormMapper.mapToNewProductForm(form)).getBody();
        ProductMediaDto productMedia = productMediaApiClient.createProductMedia(CreateMediaForProductFormBuilder.createMediaForProductForm()
                .productId(product.getId())
                .build()
        ).getBody();
        product.setMediaId(productMedia.getId());
        return productApiClient.updateProduct(product.getId(), product).getBody();
    }

    @Override
    public Long removeProduct(Long productId, String accessToken) {
        AccessTokenUserInfoDto userInfo = getUserInfoByAccessToken(accessToken);
        try {
            SellerInfo sellerInfo = sellerInfoRepository.getByUserId(userInfo.getUserId());
            SellerProduct sellerProduct = getProductFromSellerById(productId, sellerInfo);
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

    @Override
    public SellerProductDetailsDto updateSellersProductInfo(String accessToken, UpdateSellerProductForm form) {
        AccessTokenUserInfoDto userInfo = getUserInfoByAccessToken(accessToken);
        try {
            SellerInfo sellerInfo = sellerInfoRepository.getByUserId(userInfo.getUserId());
            SellerProduct sellerProduct = getProductFromSellerById(form.getSellerProductId(), sellerInfo);

            ProductDto productDto = productApiClient.getProduct(sellerProduct.getProductId()).getBody();
            List<CategoryDto> categories = productCategoryApiClient.getCategoriesByIds(form.getCategoryIds()).getBody();
            List<DiscountDto> discounts = productDiscountApiClient.getDiscountsByIds(form.getDiscountIds()).getBody();

            productDto.setCategories(categories);
            productDto.setDiscounts(discounts);
            sellerProductDetailsMapper.mapProductDto(form, productDto);
            productDto = productApiClient.updateProduct(productDto.getId(), productDto).getBody();

            SellerProductDetailsDto productDetails = sellerProductDetailsMapper.mapToDto(sellerProduct);
            sellerProductDetailsMapper.mapSellerProductDetailsDto(productDto, productDetails);
            return productDetails;
        } catch (Exception e) {
            log.error("Unable to update seller's '{}' product! {}", form.getSellerProductId(), e.getMessage());
            throw new UpdateSellerProductException(
                    "Unable to update seller's '%s' product! %s".formatted(form.getSellerProductId(), e.getMessage())
            );
        }
    }

    @Override
    public List<byte[]> loadProductImgs(String accessToken, Long sellerProductId) {
        AccessTokenUserInfoDto userInfo = getUserInfoByAccessToken(accessToken);
        SellerInfo sellerInfo = findSellerByUserId(userInfo.getUserId());
        SellerProduct sellerProduct = findProductFromSellerInfo(sellerInfo, sellerProductId);

        return productMediaApiClient.loadAllProductsImages(sellerProduct.getProductId()).getBody();
    }


    @Override
    public ProductMediaDto saveImgToProductMedia(String accessToken, Long sellerProductId, AddMediaToProductForm form) {
        AccessTokenUserInfoDto userInfo = getUserInfoByAccessToken(accessToken);
        SellerInfo sellerInfo = findSellerByUserId(userInfo.getUserId());
        SellerProduct sellerProduct = findProductFromSellerInfo(sellerInfo, sellerProductId);

        return productMediaApiClient.addImageToProduct(sellerProduct.getProductId(), form.getMultipartFile()).getBody();
    }

    @Override
    public Long removeProductImage(String accessToken, Long sellerProductId, Long imageId) {
        AccessTokenUserInfoDto userInfo = getUserInfoByAccessToken(accessToken);
        SellerInfo sellerInfo = findSellerByUserId(userInfo.getUserId());
        SellerProduct sellerProduct = findProductFromSellerInfo(sellerInfo, sellerProductId);

        return productMediaApiClient.removeImageFromProduct(sellerProduct.getProductId(), imageId).getBody();
    }

    @Override
    public List<ImgMetadataDto> getProductImagesMetadata(String accessToken, Long sellerProductId) {
        AccessTokenUserInfoDto userInfo = getUserInfoByAccessToken(accessToken);
        SellerInfo sellerInfo = findSellerByUserId(userInfo.getUserId());
        SellerProduct sellerProduct = findProductFromSellerInfo(sellerInfo, sellerProductId);

        return productMediaApiClient.getProductImagesMetadata(sellerProduct.getProductId()).getBody();
    }

    @Override
    public ImgMetadataDto getProductImageMetadata(String accessToken, Long sellerProductId, Long imageId) {
        AccessTokenUserInfoDto userInfo = getUserInfoByAccessToken(accessToken);
        SellerInfo sellerInfo = findSellerByUserId(userInfo.getUserId());
        SellerProduct sellerProduct = findProductFromSellerInfo(sellerInfo, sellerProductId);

        return productMediaApiClient.getProductImageMetadata(sellerProduct.getProductId(), imageId).getBody();
    }
    @Override
    public byte[] loadProductDock(String accessToken, Long sellerProductId, Long dockId) {
        AccessTokenUserInfoDto userInfo = getUserInfoByAccessToken(accessToken);
        SellerInfo sellerInfo = findSellerByUserId(userInfo.getUserId());
        SellerProduct sellerProduct = findProductFromSellerInfo(sellerInfo, sellerProductId);

        return productMediaApiClient.loadProductDock(sellerProduct.getProductId(), dockId).getBody();
    }

    @Override
    public List<DockMetadataDto> getProductDocksMetadata(String accessToken, Long sellerProductId) {
        AccessTokenUserInfoDto userInfo = getUserInfoByAccessToken(accessToken);
        SellerInfo sellerInfo = findSellerByUserId(userInfo.getUserId());
        SellerProduct sellerProduct = findProductFromSellerInfo(sellerInfo, sellerProductId);

        return productMediaApiClient.getProductDocksMetadata(sellerProduct.getProductId()).getBody();
    }


    @Override
    public DockMetadataDto getProductDockMetadata(String accessToken, Long sellerProductId, Long dockId) {
        AccessTokenUserInfoDto userInfo = getUserInfoByAccessToken(accessToken);
        SellerInfo sellerInfo = findSellerByUserId(userInfo.getUserId());
        SellerProduct sellerProduct = findProductFromSellerInfo(sellerInfo, sellerProductId);

        return productMediaApiClient.getProductDockMetadata(sellerProduct.getProductId(), dockId).getBody();
    }

    @Override
    public ProductMediaDto saveDockToProductMedia(String accessToken, Long sellerProductId, AddMediaToProductForm form) {
        AccessTokenUserInfoDto userInfo = getUserInfoByAccessToken(accessToken);
        SellerInfo sellerInfo = findSellerByUserId(userInfo.getUserId());
        SellerProduct sellerProduct = findProductFromSellerInfo(sellerInfo, sellerProductId);

        return productMediaApiClient.addDockToProduct(sellerProduct.getProductId(), form.getMultipartFile()).getBody();
    }

    @Override
    public Long removeProductDock(String accessToken, Long sellerProductId, Long dockId) {
        AccessTokenUserInfoDto userInfoDto = getUserInfoByAccessToken(accessToken);
        SellerInfo sellerInfo = findSellerByUserId(userInfoDto.getUserId());
        SellerProduct sellerProduct = findProductFromSellerInfo(sellerInfo, sellerProductId);

        return productMediaApiClient.removeDockFormProduct(sellerProduct.getProductId(), dockId).getBody();
    }

    private SellerProduct findProductFromSellerInfo(SellerInfo sellerInfo, Long sellerProductId) {
        return sellerInfo.getProducts().stream()
                .filter(p -> p.getId().equals(sellerProductId))
                .findFirst()
                .orElseThrow(() -> {
                    log.warn("Product '{}' not belong to the seller '{}'! ", sellerProductId, sellerInfo.getProducts());
                    return new ProductNotBelongToSellerException(
                            "Product '%s' not belong to the seller '%s'! ".formatted(sellerProductId, sellerInfo.getProducts())
                    );
                });
    }

    private static SellerProduct getProductFromSellerById(Long productId, SellerInfo sellerInfo) {
        return sellerInfo.getProducts().stream()
                .filter(product -> product.getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> {
                    log.error("Product doesn't belong to seller!");
                    return new ProductNotBelongToSellerException("Product doesn't belong to seller!");
                });
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
                    .ifPresent(prod -> sellerProductDetailsMapper.mapSellerProductDetailsDto(prod, prodDet))
        );
        return productDetails;
    }

    private AccessTokenUserInfoDto getUserInfoByAccessToken(String accessToken) {
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

    private SellerInfo findSellerByUserId(Long userId) {
        return sellerInfoRepository.findByUserId(userId).orElseThrow(() -> {
            log.warn("Unable find seller with id '{}'!", userId);
            return new EntityNotFoundRepositoryException("Unable find seller with id '%s'!".formatted(userId));
        });
    }



}
