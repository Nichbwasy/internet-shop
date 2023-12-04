package com.shop.seller.service.utils;

import com.shop.authorization.dto.api.user.SellerUserDataDto;
import com.shop.common.utils.all.generator.StringGenerator;
import com.shop.seller.model.SellerInfo;

import java.util.ArrayList;
import java.util.Random;


/**
 * Generates random model/dto objects. (Use only in the test classes!)
 */
public final class TestDataGenerator {

    private final static Random random = new Random();

    public static SellerInfo generateSellerInfo() {
        SellerInfo sellerInfo = new SellerInfo();
        sellerInfo.setId(random.nextLong(0, 1000));
        sellerInfo.setUserId(random.nextLong());
        sellerInfo.setRating(random.nextFloat());
        sellerInfo.setDescription(StringGenerator.generate(random.nextInt(16, 32)));
        sellerInfo.setProducts(new ArrayList<>());
        return sellerInfo;
    }

    public static SellerUserDataDto generateSellerUserDataDto() {
        SellerUserDataDto sellerUserDataDto = new SellerUserDataDto();
        sellerUserDataDto.setId(random.nextLong(1, 1000));
        sellerUserDataDto.setLogin(StringGenerator.generate(random.nextInt(8, 16)));
        sellerUserDataDto.setEmail(StringGenerator.generate(random.nextInt(12, 32)));
        return sellerUserDataDto;
    }

}
