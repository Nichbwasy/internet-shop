package com.shop.seller.service;

import com.shop.seller.dto.SellerInfoDto;
import com.shop.seller.dto.control.RegisterNewSellerForm;
import com.shop.seller.dto.control.SellerDetailsDto;

import java.util.List;

public interface AdminSellersControlService {

    SellerDetailsDto getSellerInfo(Long sellerId);
    List<SellerInfoDto> getSellersInfoFromPage(Integer page);
    SellerDetailsDto registerNewSeller(String accessToken, RegisterNewSellerForm form);
    Long removeSellerFromSystem(Long id);

}
