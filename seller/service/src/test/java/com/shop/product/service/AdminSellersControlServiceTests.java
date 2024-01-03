package com.shop.product.service;

import com.shop.authorization.client.UserDataApiClient;
import com.shop.authorization.common.data.builder.SellerUserDataDtoBuilder;
import com.shop.authorization.dto.api.user.SellerUserDataDto;
import com.shop.common.utils.all.exception.dao.EntityNotFoundRepositoryException;
import com.shop.product.client.ProductApiClient;
import com.shop.product.service.config.AdminSellersControlServiceTestConfiguration;
import com.shop.seller.common.test.data.builder.RegisterNewSellerFormBuilder;
import com.shop.seller.common.test.data.builder.SellerInfoBuilder;
import com.shop.seller.common.test.data.builder.SellerProductBuilder;
import com.shop.seller.dao.SellerInfoRepository;
import com.shop.seller.dao.SellerProductRepository;
import com.shop.seller.dto.SellerInfoDto;
import com.shop.seller.dto.control.RegisterNewSellerForm;
import com.shop.seller.dto.control.SellerDetailsDto;
import com.shop.seller.model.SellerInfo;
import com.shop.seller.service.AdminSellersControlService;
import com.shop.seller.service.exception.control.SellerRegistrationException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.List;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = AdminSellersControlServiceTestConfiguration.class)
public class AdminSellersControlServiceTests {

    @Autowired
    private SellerInfoRepository sellerInfoRepository;
    @Autowired
    private UserDataApiClient userDataApiClient;
    @Autowired
    private ProductApiClient productApiClient;
    @Autowired
    private AdminSellersControlService controlService;
    @Autowired
    private SellerProductRepository sellerProductRepository;

    @Test
    public void getSellerInfoTest() {
        SellerInfo sellerInfo = SellerInfoBuilder.sellerInfo().build();
        SellerUserDataDto sellerData = SellerUserDataDtoBuilder.sellerUserDataDto().id(sellerInfo.getUserId()).build();

        Mockito.when(sellerInfoRepository.existsById(sellerInfo.getId())).thenReturn(true);
        Mockito.when(sellerInfoRepository.getReferenceById(sellerInfo.getId())).thenReturn(sellerInfo);
        Mockito.when(userDataApiClient.getSellerInfo(sellerInfo.getUserId()))
                .thenReturn(ResponseEntity.ok().body(sellerData));

        SellerDetailsDto result = controlService.getSellerInfo(sellerInfo.getId());

        Assertions.assertEquals(sellerInfo.getId(), result.getId());
        Assertions.assertEquals(sellerData.getId(), result.getUserId());
        Assertions.assertEquals(sellerData.getLogin(), result.getLogin());
        Assertions.assertEquals(sellerData.getEmail(), result.getEmail());
    }

    @Test
    public void getNotExistedSellerInfoTest() {
        Mockito.when(sellerInfoRepository.existsById(Mockito.anyLong())).thenReturn(false);

        Assertions.assertThrows(EntityNotFoundRepositoryException.class, () -> controlService.getSellerInfo(1L));
    }

    @Test
    public void getSellersInfoFromPageTest() {
        List<SellerInfo> sellers = List.of(
                SellerInfoBuilder.sellerInfo().build(),
                SellerInfoBuilder.sellerInfo().build()
        );
        Page<SellerInfo> page = new PageImpl<>(sellers);

        Mockito.when(sellerInfoRepository.findAll(Mockito.any(PageRequest.class))).thenReturn(page);

        List<SellerInfoDto> result = controlService.getSellersInfoFromPage(1);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.size());
    }

    @Test
    public void registerNewSellerTest() {
        RegisterNewSellerForm form = RegisterNewSellerFormBuilder.registerNewSellerForm().build();
        SellerUserDataDto sellerData = SellerUserDataDtoBuilder.sellerUserDataDto().id(form.getUserId()).build();

        Mockito.when(sellerInfoRepository.existsByUserId(form.getUserId())).thenReturn(false);
        Mockito.when(userDataApiClient.makeUserSeller(Mockito.anyString(), Mockito.eq(form.getUserId()))).thenReturn(ResponseEntity.ok().body(sellerData));
        Mockito.when(sellerInfoRepository.save(Mockito.any(SellerInfo.class))).thenAnswer(a ->
                {
                    SellerInfo sellerInfo = a.getArgument(0);
                    sellerInfo.setId(1L);
                    return sellerInfo;
                }
        );

        SellerDetailsDto result = controlService.registerNewSeller("", form);

        Assertions.assertNotNull(result.getId());
        Assertions.assertEquals(sellerData.getId(), result.getUserId());
        Assertions.assertEquals(sellerData.getLogin(), result.getLogin());
        Assertions.assertEquals(sellerData.getEmail(), result.getEmail());
        Assertions.assertTrue(result.getRating() > 0 && result.getRating() < 10);
        Assertions.assertTrue(result.getRegistrationDate().isBefore(LocalDateTime.now()));
    }

    @Test
    public void registerAlreadyExistedSellerTest() {
        RegisterNewSellerForm form = RegisterNewSellerFormBuilder.registerNewSellerForm().build();

        Mockito.when(sellerInfoRepository.existsByUserId(form.getUserId())).thenReturn(true);

        Assertions.assertThrows(SellerRegistrationException.class, () -> controlService.registerNewSeller("", form));
    }

    @Test
    public void removeSellerFromSystemTest() {
        SellerInfo sellerInfo = SellerInfoBuilder.sellerInfo().build();
        sellerInfo.setProducts(List.of(
                SellerProductBuilder.sellerProduct().build(),
                SellerProductBuilder.sellerProduct().build()
        ));

        Mockito.when(sellerInfoRepository.getReferenceById(sellerInfo.getId())).thenReturn(sellerInfo);
        Mockito.when(productApiClient.removeProducts(Mockito.anyList()))
                .thenAnswer(a -> ResponseEntity.ok().body(a.getArgument(0)));
        Mockito.doNothing().when(sellerProductRepository).deleteAllById(Mockito.anyList());

        Long result = controlService.removeSellerFromSystem(sellerInfo.getId());

        Assertions.assertNotNull(result);
        Assertions.assertEquals(sellerInfo.getId(), result);
    }

    @Test
    public void removeNotExistedSellerFromSystemTest() {
        Mockito.when(sellerInfoRepository.getReferenceById(Mockito.anyLong())).thenThrow(EntityNotFoundException.class);

        Assertions.assertThrows(EntityNotFoundException.class, () -> controlService.removeSellerFromSystem(1L));
    }

}
