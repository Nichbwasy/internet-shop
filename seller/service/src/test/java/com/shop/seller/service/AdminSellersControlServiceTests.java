package com.shop.seller.service;

import com.shop.authorization.client.UserDataApiClient;
import com.shop.seller.dao.SellerInfoRepository;
import com.shop.seller.service.config.AdminSellersControlServiceTestsConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Random;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = AdminSellersControlServiceTestsConfiguration.class)
public class AdminSellersControlServiceTests {

    @Autowired
    private UserDataApiClient userDataApiClient;
    @Autowired
    private SellerInfoRepository sellerInfoRepository;
    @Autowired
    private AdminSellersControlService controlService;
    private final Random random = new Random();

    @Test
    public void getSellerInfoTest() {
        System.out.println();
//        SellerInfo sellerInfo = TestDataGenerator.generateSellerInfo();
//        SellerUserDataDto sellerUserData = TestDataGenerator.generateSellerUserDataDto();
//        sellerUserData.setId(sellerInfo.getUserId());
//
//        Mockito.when(sellerInfoRepository.existsById(sellerInfo.getId())).thenReturn(true);
//        Mockito.when(sellerInfoRepository.getReferenceById(sellerInfo.getId())).thenReturn(sellerInfo);
//        Mockito.when(userDataApiClient.getSellerInfo(sellerInfo.getUserId()))
//                .thenReturn(ResponseEntity.ok().body(sellerUserData));
//
//        SellerDetailsDto result = controlService.getSellerInfo(sellerInfo.getId());
//
//        Assertions.assertNotNull(result);
//        Assertions.assertEquals(sellerInfo.getId(), result.getId());
//        Assertions.assertEquals(sellerUserData.getId(), result.getUserId());
//        Assertions.assertEquals(sellerUserData.getLogin(), result.getLogin());
//        Assertions.assertEquals(sellerUserData.getEmail(), result.getEmail());
//        Assertions.assertEquals(sellerInfo.getDescription(), result.getDescription());
    }




}
