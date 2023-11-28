package com.shop.seller.service;

import com.shop.seller.dto.SellerInfoDto;
import com.shop.seller.dto.control.RegisterNewSellerForm;
import com.shop.seller.dto.control.SellerDetailsDto;

import java.util.List;

public interface SellersAdminControlService {

    SellerDetailsDto getSellerInfo(Long sellerId);
    List<SellerInfoDto> getSellersInfoFromPage(Integer page);
    SellerDetailsDto registerNewSeller(RegisterNewSellerForm form);
    List<Long> removeSellerFromSystem(Long id);

}
