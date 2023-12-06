package com.shop.seller.controller.utils;

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

public final class GenTestData {

    private final static Random random = new Random();

    public static SellerInfo generateSellerInfo() {
        SellerInfo sellerInfo = new SellerInfo();
        sellerInfo.setUserId(random.nextLong(1, 1000));
        sellerInfo.setRating(random.nextFloat(0, 10));
        sellerInfo.setDescription(StringGenerator.generate(random.nextInt(24, 48)));
        sellerInfo.setRegistrationDate(LocalDateTime.now().minusHours(random.nextLong(1, 1000)));
        sellerInfo.setProducts(new ArrayList<>());
        return sellerInfo;
    }

    public static SellerProduct generateSellerProduct() {
        SellerProduct sellerProduct = new SellerProduct();
        sellerProduct.setProductId(random.nextLong(1, 1000));
        return sellerProduct;
    }

    public static AccessTokenUserInfoDto generateUserInfo() {
        AccessTokenUserInfoDto userInfoDto = new AccessTokenUserInfoDto();
        userInfoDto.setUserId(random.nextLong(1, 1000));
        userInfoDto.setUserLogin(StringGenerator.generate(random.nextInt(8, 12)));
        userInfoDto.setUserLogin(StringGenerator.generate(random.nextInt(8, 12)) + "@somemail.com");
        return userInfoDto;
    }

    public static ProductDto generateProduct() {
        ProductDto productDto = new ProductDto();
        productDto.setName(StringGenerator.generate(random.nextInt(8, 16)));
        productDto.setApprovalStatus(ApprovalStatuses.APPROVED);
        productDto.setId(random.nextLong(1, 1000));
        productDto.setCount(random.nextInt(1, 100));
        productDto.setDescription(StringGenerator.generate(random.nextInt(24, 48)));
        productDto.setCreatedTime(LocalDateTime.now().minusHours(random.nextInt(1, 1000)));
        productDto.setDiscounts(new ArrayList<>());
        productDto.setCategories(new ArrayList<>());
        return productDto;
    }

    public static SellerUserDataDto generateSellersUserData() {
        SellerUserDataDto sellerUserDataDto = new SellerUserDataDto();
        sellerUserDataDto.setId(random.nextLong(1, 1000));
        sellerUserDataDto.setLogin(StringGenerator.generate(random.nextInt(8, 12)));
        sellerUserDataDto.setEmail(StringGenerator.generate(random.nextInt(8, 12)) + "@somemail.com");
        return sellerUserDataDto;
    }

    public static RegisterNewSellerForm generateNewSellerForm() {
        RegisterNewSellerForm form = new RegisterNewSellerForm();
        form.setUserId(random.nextLong(1, 1000));
        return form;
    }
}
