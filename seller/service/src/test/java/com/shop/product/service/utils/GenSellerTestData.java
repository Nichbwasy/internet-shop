package com.shop.product.service.utils;

import com.shop.authorization.dto.api.user.SellerUserDataDto;
import com.shop.authorization.dto.token.AccessTokenUserInfoDto;
import com.shop.common.utils.all.consts.ApprovalStatuses;
import com.shop.common.utils.all.generator.StringGenerator;
import com.shop.product.dto.ProductDto;
import com.shop.seller.dto.control.RegisterNewSellerForm;
import com.shop.seller.model.SellerInfo;
import com.shop.seller.model.SellerProduct;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Random;

/**
 * Generates random seller's data. (Use only in a tests!)
 */
public final class GenSellerTestData {

    private final static Random random = new Random();

    public static SellerInfo generateSellerInfo() {
        SellerInfo sellerInfo = new SellerInfo();
        sellerInfo.setId(random.nextLong(1, 1000));
        sellerInfo.setRating(random.nextFloat(0, 5));
        sellerInfo.setUserId(random.nextLong(1, 1000));
        sellerInfo.setDescription(StringGenerator.generate(random.nextInt(16, 32)));
        sellerInfo.setProducts(new ArrayList<>());
        return sellerInfo;
    }

    public static SellerUserDataDto generateSellerData() {
        SellerUserDataDto sellerData = new SellerUserDataDto();
        sellerData.setId(random.nextLong(1, 1000));
        sellerData.setLogin(StringGenerator.generate(random.nextInt(8, 12)));
        sellerData.setEmail(StringGenerator.generate(random.nextInt(8, 12)) + "@somemail.com");
        return sellerData;
    }

    public static RegisterNewSellerForm generateSellerRegisterForm() {
        RegisterNewSellerForm form = new RegisterNewSellerForm();
        form.setUserId(random.nextLong(1, 1000));
        return form;
    }

    public static SellerProduct generateSellerProduct() {
        SellerProduct sellerProduct = new SellerProduct();
        sellerProduct.setId(random.nextLong(1, 1000));
        sellerProduct.setProductId(random.nextLong(1, 1000));
        return sellerProduct;
    }

    public static ProductDto generateProduct() {
        ProductDto productDto = new ProductDto();
        productDto.setId(random.nextLong(1, 1000));
        productDto.setName(StringGenerator.generate(random.nextInt(12, 24)));
        productDto.setApprovalStatus(ApprovalStatuses.APPROVED);
        productDto.setDescription(StringGenerator.generate(random.nextInt(128, 255)));
        productDto.setCode(StringGenerator.generate(64));
        productDto.setCount(random.nextInt(1,100));
        productDto.setCreatedTime(LocalDateTime.now());
        productDto.setCategories(new ArrayList<>());
        productDto.setDiscounts(new ArrayList<>());
        return productDto;
    }

    public static AccessTokenUserInfoDto generateAccessTokenUserData() {
        AccessTokenUserInfoDto userInfoDto = new AccessTokenUserInfoDto();
        userInfoDto.setUserId(random.nextLong(1, 1000));
        userInfoDto.setUserLogin(StringGenerator.generate(random.nextInt(8, 12)));
        return userInfoDto;
    }

}
