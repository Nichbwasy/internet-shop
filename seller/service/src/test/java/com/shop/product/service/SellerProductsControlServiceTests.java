package com.shop.product.service;

import com.shop.authorization.client.TokensApiClient;
import com.shop.authorization.common.data.builder.AccessTokenUserInfoBuilder;
import com.shop.authorization.dto.token.AccessTokenUserInfoDto;
import com.shop.media.client.ProductMediaApiClient;
import com.shop.media.common.data.builder.ProductMediaDtoBuilder;
import com.shop.media.dto.ProductMediaDto;
import com.shop.media.dto.form.CreateMediaForProductForm;
import com.shop.product.client.ProductApiClient;
import com.shop.product.common.data.builder.ProductDtoBuilder;
import com.shop.product.dto.ProductDto;
import com.shop.product.dto.form.product.NewProductForm;
import com.shop.product.service.config.SellerProductsControlServiceTestConfiguration;
import com.shop.product.service.utils.mapper.SellerServiceTestMapper;
import com.shop.seller.common.test.data.builder.CreateProductFormBuilder;
import com.shop.seller.common.test.data.builder.SellerInfoBuilder;
import com.shop.seller.common.test.data.builder.SellerProductBuilder;
import com.shop.seller.dao.SellerInfoRepository;
import com.shop.seller.dao.SellerProductRepository;
import com.shop.seller.dto.control.CreateProductForm;
import com.shop.seller.dto.control.SellerProductDetailsDto;
import com.shop.seller.model.SellerInfo;
import com.shop.seller.model.SellerProduct;
import com.shop.seller.service.SellerProductsControlService;
import com.shop.seller.service.exception.control.AddNewProductException;
import com.shop.seller.service.exception.control.GetSellerProductsDetailsException;
import com.shop.seller.service.exception.control.GetUserInfoApiClientException;
import com.shop.seller.service.exception.control.RemoveProductFromSellerException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = SellerProductsControlServiceTestConfiguration.class)
public class SellerProductsControlServiceTests {
    @Autowired
    private SellerInfoRepository sellerInfoRepository;
    @Autowired
    private SellerProductRepository sellerProductRepository;
    @Autowired
    private ProductApiClient productApiClient;
    @Autowired
    private TokensApiClient tokensApiClient;
    @Autowired
    private ProductMediaApiClient productMediaApiClient;
    @Autowired
    private SellerProductsControlService controlService;

    @Test
    public void showAllSellersProductsTest() {
        AccessTokenUserInfoDto tokenUserInfo = AccessTokenUserInfoBuilder.accessTokenUserInfoDto().build();
        List<ProductDto> products = List.of(ProductDtoBuilder.productDto().build(), ProductDtoBuilder.productDto().build());
        SellerInfo sellerInfo = SellerInfoBuilder.sellerInfo()
                .userId(tokenUserInfo.getUserId())
                .products(List.of(
                        SellerProductBuilder.sellerProduct().productId(products.get(0).getId()).build(),
                        SellerProductBuilder.sellerProduct().productId(products.get(1).getId()).build()
                )).build();

        Mockito.when(tokensApiClient.getTokenUserInfo(Mockito.anyString()))
                .thenReturn(ResponseEntity.ok().body(tokenUserInfo));
        Mockito.when(sellerInfoRepository.getByUserId(tokenUserInfo.getUserId())).thenReturn(sellerInfo);
        Mockito.when(productApiClient.getProductsByIds(Mockito.anyInt(),
                Mockito.anyList())).thenReturn(ResponseEntity.ok().body(products));

        List<SellerProductDetailsDto> result = controlService.showAllSellersProducts(1, "some_access_token");

        Assertions.assertEquals(2, result.size());
        Assertions.assertTrue(result.stream().allMatch(
                r -> products.stream().anyMatch(p -> p.getId().equals(r.getProductId()))
        ));
    }

    @Test
    public void showAllSellersProductsNotExistTest() {
        Mockito.when(tokensApiClient.getTokenUserInfo(Mockito.anyString())).thenThrow(RuntimeException.class);

        Assertions.assertThrows(GetUserInfoApiClientException.class,
                () -> controlService.showAllSellersProducts(1, "some_access_token"));

    }

    @Test
    public void showSellerProductTest() {
        AccessTokenUserInfoDto tokenUserInfo = AccessTokenUserInfoBuilder.accessTokenUserInfoDto().build();
        SellerInfo sellerInfo = SellerInfoBuilder.sellerInfo()
                .id(tokenUserInfo.getUserId())
                .products(List.of(
                        SellerProductBuilder.sellerProduct().build(),
                        SellerProductBuilder.sellerProduct().build()
                )).build();
        Long searchId = sellerInfo.getProducts().get(0).getId();
        Long searchedProductId = sellerInfo.getProducts().get(0).getProductId();
        ProductDto product = ProductDtoBuilder.productDto().id(searchedProductId).build();

        Mockito.when(tokensApiClient.getTokenUserInfo(Mockito.anyString()))
                .thenReturn(ResponseEntity.ok().body(tokenUserInfo));
        Mockito.when(sellerInfoRepository.getByUserId(tokenUserInfo.getUserId())).thenReturn(sellerInfo);
        Mockito.when(productApiClient.getProduct(searchedProductId)).thenReturn(ResponseEntity.ok().body(product));

        SellerProductDetailsDto result = controlService.showSellerProduct(searchId, "some_access_token");

        Assertions.assertEquals(searchId, result.getId());
    }

    @Test
    public void showSellerNotExistedProductTest() {
        AccessTokenUserInfoDto tokenUserInfo = AccessTokenUserInfoBuilder.accessTokenUserInfoDto().build();
        SellerInfo sellerInfo = SellerInfoBuilder.sellerInfo()
                .id(tokenUserInfo.getUserId())
                .products(List.of(
                        SellerProductBuilder.sellerProduct().build(),
                        SellerProductBuilder.sellerProduct().build()
                )).build();

        Mockito.when(tokensApiClient.getTokenUserInfo(Mockito.anyString()))
                .thenReturn(ResponseEntity.ok().body(tokenUserInfo));
        Mockito.when(sellerInfoRepository.getByUserId(tokenUserInfo.getUserId())).thenReturn(sellerInfo);

        Assertions.assertThrows(GetSellerProductsDetailsException.class,
                () -> controlService.showSellerProduct(1001L, "some_access_token"));
    }

    @Test
    public void showSellerNotExistedUserProductTest() {
        Mockito.when(tokensApiClient.getTokenUserInfo(Mockito.anyString())).thenThrow(RuntimeException.class);

        Assertions.assertThrows(GetUserInfoApiClientException.class,
                () -> controlService.showSellerProduct(1L, "some_access_token"));
    }

    @Test
    public void createNewProductTest() {
        AccessTokenUserInfoDto userInfo = AccessTokenUserInfoBuilder.accessTokenUserInfoDto().build();
        CreateProductForm form = CreateProductFormBuilder.createProductForm().build();
        ProductDto product = ProductDtoBuilder.productDto()
                .name(form.getName())
                .description(form.getDescription())
                .price(form.getPrice())
                .count(form.getCount())
                .build();
        ProductMediaDto productMedia = ProductMediaDtoBuilder.productMediaDto().productId(product.getId()).build();
        SellerInfo sellerInfo = SellerInfoBuilder.sellerInfo()
                .userId(userInfo.getUserId()).build();

        Mockito.when(tokensApiClient.getTokenUserInfo(Mockito.anyString())).thenReturn(ResponseEntity.ok().body(userInfo));
        Mockito.when(productApiClient.createProduct(Mockito.any(NewProductForm.class)))
                .thenReturn(ResponseEntity.ok().body(product));
        Mockito.when(productMediaApiClient.createProductMedia(Mockito.any(CreateMediaForProductForm.class)))
                        .thenReturn(ResponseEntity.ok().body(productMedia));
        Mockito.when(productApiClient.updateProduct(Mockito.anyLong(), Mockito.any(ProductDto.class)))
                        .thenAnswer(a -> ResponseEntity.ok().body(a.getArgument(1)));
        Mockito.when(sellerProductRepository.save(Mockito.any(SellerProduct.class)))
                .thenAnswer(a -> {
                    SellerProduct sellerProduct = a.getArgument(0);
                    sellerProduct.setId(1L);
                    return sellerProduct;
                });
        Mockito.when(sellerInfoRepository.getByUserId(userInfo.getUserId())).thenReturn(sellerInfo);

        SellerProductDetailsDto result = controlService.createNewProduct(form, "some_access_token");

        Assertions.assertEquals(form.getName(), result.getName());
        Assertions.assertEquals(form.getPrice(), result.getPrice());
        Assertions.assertEquals(form.getCount(), result.getCount());
    }

    @Test
    public void createNewProductSellerNotExistsTest() {
        AccessTokenUserInfoDto userInfo = AccessTokenUserInfoBuilder.accessTokenUserInfoDto().build();
        CreateProductForm form = CreateProductFormBuilder.createProductForm().build();

        Mockito.when(tokensApiClient.getTokenUserInfo(Mockito.anyString())).thenReturn(ResponseEntity.ok().body(userInfo));
        Mockito.when(productApiClient.createProduct(Mockito.any(NewProductForm.class)))
                .thenAnswer(a -> ResponseEntity.ok().body(SellerServiceTestMapper.INSTANCE.mapNewProductFormToDto(a.getArgument(0))));
        Mockito.when(sellerProductRepository.save(Mockito.any(SellerProduct.class)))
                .thenAnswer(a -> {
                    SellerProduct sellerProduct = a.getArgument(0);
                    sellerProduct.setId(1L);
                    return sellerProduct;
                });
        Mockito.when(sellerInfoRepository.getByUserId(Mockito.anyLong())).thenThrow(RuntimeException.class);

        Assertions.assertThrows(AddNewProductException.class,
                () -> controlService.createNewProduct(form, "some_access_token"));
    }

    @Test
    public void createNewProductRepositoryExceptionTest() {
        AccessTokenUserInfoDto userInfo = AccessTokenUserInfoBuilder.accessTokenUserInfoDto().build();
        CreateProductForm form = CreateProductFormBuilder.createProductForm().build();

        Mockito.when(tokensApiClient.getTokenUserInfo(Mockito.anyString())).thenReturn(ResponseEntity.ok().body(userInfo));
        Mockito.when(productApiClient.createProduct(Mockito.any(NewProductForm.class)))
                .thenAnswer(a -> ResponseEntity.ok().body(SellerServiceTestMapper.INSTANCE.mapNewProductFormToDto(a.getArgument(0))));
        Mockito.when(sellerProductRepository.save(Mockito.any(SellerProduct.class))).thenThrow(RuntimeException.class);

        Assertions.assertThrows(AddNewProductException.class,
                () -> controlService.createNewProduct(form, "some_access_token"));
    }

    @Test
    public void createNewProductProductClientExceptionTest() {
        AccessTokenUserInfoDto userInfo = AccessTokenUserInfoBuilder.accessTokenUserInfoDto().build();
        CreateProductForm form = CreateProductFormBuilder.createProductForm().build();

        Mockito.when(tokensApiClient.getTokenUserInfo(Mockito.anyString())).thenReturn(ResponseEntity.ok().body(userInfo));
        Mockito.when(productApiClient.createProduct(Mockito.any(NewProductForm.class))).thenThrow(RuntimeException.class);

        Assertions.assertThrows(AddNewProductException.class,
                () -> controlService.createNewProduct(form, "some_access_token"));
    }

    @Test
    public void createNewProductUserClientExceptionTest() {
        CreateProductForm form = CreateProductFormBuilder.createProductForm().build();

        Mockito.when(tokensApiClient.getTokenUserInfo(Mockito.anyString())).thenThrow(RuntimeException.class);

        Assertions.assertThrows(GetUserInfoApiClientException.class,
                () -> controlService.createNewProduct(form, "some_access_token"));
    }

    @Test
    public void createNewProductNullDataTest() {
        AccessTokenUserInfoDto userInfo = AccessTokenUserInfoBuilder.accessTokenUserInfoDto().build();
        CreateProductForm form = new CreateProductForm();

        Mockito.when(tokensApiClient.getTokenUserInfo(Mockito.anyString())).thenReturn(ResponseEntity.ok().body(userInfo));
        Mockito.when(productApiClient.createProduct(Mockito.any(NewProductForm.class)))
                .thenAnswer(a -> ResponseEntity.ok().body(SellerServiceTestMapper.INSTANCE.mapNewProductFormToDto(a.getArgument(0))));
        Mockito.when(sellerProductRepository.save(Mockito.any(SellerProduct.class))).thenThrow(RuntimeException.class);

        Assertions.assertThrows(AddNewProductException.class,
                () -> controlService.createNewProduct(form, "some_access_token"));
    }

    @Test
    public void createNullNewProductTest() {
        AccessTokenUserInfoDto userInfo = AccessTokenUserInfoBuilder.accessTokenUserInfoDto().build();

        Mockito.when(tokensApiClient.getTokenUserInfo(Mockito.anyString())).thenReturn(ResponseEntity.ok().body(userInfo));
        Mockito.when(productApiClient.createProduct(Mockito.any(NewProductForm.class)))
                .thenAnswer(a -> ResponseEntity.ok().body(SellerServiceTestMapper.INSTANCE.mapNewProductFormToDto(a.getArgument(0))));
        Mockito.when(sellerProductRepository.save(Mockito.any(SellerProduct.class))).thenThrow(RuntimeException.class);

        Assertions.assertThrows(AddNewProductException.class,
                () -> controlService.createNewProduct(null, "some_access_token"));
    }

    @Test
    public void removeProductTest() {
        AccessTokenUserInfoDto userInfo = AccessTokenUserInfoBuilder.accessTokenUserInfoDto().build();
        SellerProduct sellerProduct = SellerProductBuilder.sellerProduct().build();
        SellerInfo sellerInfo = SellerInfoBuilder.sellerInfo()
                .userId(userInfo.getUserId())
                .products(List.of(sellerProduct))
                .build();

        Mockito.when(tokensApiClient.getTokenUserInfo(Mockito.anyString())).thenReturn(ResponseEntity.ok().body(userInfo));
        Mockito.when(sellerInfoRepository.getByUserId(userInfo.getUserId())).thenReturn(sellerInfo);
        Mockito.when(productApiClient.removeProduct(sellerProduct.getProductId()))
                .thenReturn(ResponseEntity.ok().body(sellerProduct.getProductId()));
        Mockito.doNothing().when(sellerProductRepository).deleteById(sellerProduct.getId());

        Assertions.assertEquals(sellerProduct.getId(),
                controlService.removeProduct(sellerProduct.getId(), "some_token_imitation"));
    }

    @Test
    public void removeNotExistedProductTest() {
        AccessTokenUserInfoDto userInfo = AccessTokenUserInfoBuilder.accessTokenUserInfoDto().build();
        SellerProduct sellerProduct = SellerProductBuilder.sellerProduct().build();
        SellerInfo sellerInfo = SellerInfoBuilder.sellerInfo()
                .userId(userInfo.getUserId())
                .build();

        Mockito.when(tokensApiClient.getTokenUserInfo(Mockito.anyString())).thenReturn(ResponseEntity.ok().body(userInfo));
        Mockito.when(sellerInfoRepository.getByUserId(userInfo.getUserId())).thenReturn(sellerInfo);

        Assertions.assertThrows(RemoveProductFromSellerException.class,
                () -> controlService.removeProduct(sellerProduct.getId(), "some_token_imitation"));
    }

    @Test
    public void removeProductRepositoryExceptionTest() {
        AccessTokenUserInfoDto userInfo = AccessTokenUserInfoBuilder.accessTokenUserInfoDto().build();

        Mockito.when(tokensApiClient.getTokenUserInfo(Mockito.anyString())).thenReturn(ResponseEntity.ok().body(userInfo));
        Mockito.when(sellerInfoRepository.getByUserId(userInfo.getUserId())).thenThrow(RuntimeException.class);

        Assertions.assertThrows(RemoveProductFromSellerException.class,
                () -> controlService.removeProduct(1L, "some_token_imitation"));
    }

    @Test
    public void removeProductProductClientExceptionExceptionTest() {
        AccessTokenUserInfoDto userInfo = AccessTokenUserInfoBuilder.accessTokenUserInfoDto().build();
        SellerProduct sellerProduct = SellerProductBuilder.sellerProduct().build();
        SellerInfo sellerInfo = SellerInfoBuilder.sellerInfo()
                .userId(userInfo.getUserId())
                .products(List.of(sellerProduct))
                .build();

        Mockito.when(tokensApiClient.getTokenUserInfo(Mockito.anyString())).thenReturn(ResponseEntity.ok().body(userInfo));
        Mockito.when(sellerInfoRepository.getByUserId(userInfo.getUserId())).thenReturn(sellerInfo);
        Mockito.when(productApiClient.removeProduct(Mockito.anyLong())).thenThrow(RuntimeException.class);

        Assertions.assertThrows(RemoveProductFromSellerException.class,
                () -> controlService.removeProduct(sellerProduct.getId(), "some_token_imitation"));
    }

    @Test
    public void removeProductTokenClientExceptionTest() {
        Mockito.when(tokensApiClient.getTokenUserInfo(Mockito.anyString())).thenThrow(RuntimeException.class);

        Assertions.assertThrows(GetUserInfoApiClientException.class,
                () -> controlService.removeProduct(1L, "some_token_imitation"));
    }

    @Test
    public void removeProductNullDataTest() {
        Mockito.when(tokensApiClient.getTokenUserInfo(Mockito.anyString())).thenThrow(RuntimeException.class);

        Assertions.assertThrows(GetUserInfoApiClientException.class,
                () -> controlService.removeProduct(null, null));
    }

}
