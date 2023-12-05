package com.shop.seller.service.impl;

import com.shop.authorization.client.UserDataApiClient;
import com.shop.authorization.dto.api.user.SellerUserDataDto;
import com.shop.common.utils.all.exception.dao.EntityGetRepositoryException;
import com.shop.common.utils.all.exception.dao.EntityNotFoundRepositoryException;
import com.shop.product.client.ProductApiClient;
import com.shop.seller.dao.SellerInfoRepository;
import com.shop.seller.dao.SellerProductRepository;
import com.shop.seller.dto.SellerInfoDto;
import com.shop.seller.dto.control.RegisterNewSellerForm;
import com.shop.seller.dto.control.SellerDetailsDto;
import com.shop.seller.model.SellerInfo;
import com.shop.seller.model.SellerProduct;
import com.shop.seller.service.AdminSellersControlService;
import com.shop.seller.service.exception.control.GetSellerDataUserApiClientException;
import com.shop.seller.service.exception.control.SellerRegistrationException;
import com.shop.seller.service.mapper.SellerDetailsMapper;
import com.shop.seller.service.mapper.SellerInfoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AdminSellersControlServiceImpl implements AdminSellersControlService {

    @Value("${admin.control.sellers.info.page.size}")
    private Integer SELLERS_INFO_PAGE_SIZE;

    private final SellerInfoRepository sellerInfoRepository;
    private final SellerProductRepository sellerProductRepository;
    private final UserDataApiClient userDataApiClient;
    private final ProductApiClient productApiClient;
    private final SellerInfoMapper sellerInfoMapper;
    private final SellerDetailsMapper sellerDetailsMapper;

    @Override
    public SellerDetailsDto getSellerInfo(Long sellerId) {
        checkIfSellerExists(sellerId);
        SellerInfo sellerInfo = sellerInfoRepository.getReferenceById(sellerId);
        SellerUserDataDto sellerData = getSellerDataFromAuthorizationService(sellerInfo);

        SellerDetailsDto sellerDetails = sellerDetailsMapper.mapToDto(sellerInfo);
        sellerDetailsMapper.mapSellerUserData(sellerData, sellerDetails);
        return sellerDetails;
    }

    @Override
    public List<SellerInfoDto> getSellersInfoFromPage(Integer page) {
        try {
            PageRequest pageRequest = PageRequest.of(page - 1, SELLERS_INFO_PAGE_SIZE);
            return sellerInfoRepository.findAll(pageRequest).stream()
                    .map(sellerInfoMapper::mapToDto)
                    .toList();
        } catch (Exception e) {
            log.error("Exception while getting list of sellers! {}", e.getMessage());
            throw new EntityGetRepositoryException(
                    "Exception while getting list of sellers! %s".formatted(e.getMessage())
            );
        }
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public SellerDetailsDto registerNewSeller(RegisterNewSellerForm form) {
        if (sellerInfoRepository.existsByUserId(form.getUserId())) {
            log.warn("User '{}' already the seller!", form.getUserId());
            throw new SellerRegistrationException("User '%s' already the seller!".formatted(form.getUserId()));
        }
        SellerUserDataDto sellerData = makeUserAsSellerAndGetData(form);
        SellerInfo sellerInfo = createNewSellerInfo(sellerData);

        SellerDetailsDto sellerDetails = sellerDetailsMapper.mapToDto(sellerInfo);
        sellerDetailsMapper.mapSellerUserData(sellerData, sellerDetails);
        return sellerDetails;
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Long removeSellerFromSystem(Long id) {
        SellerInfo sellerInfo = sellerInfoRepository.getReferenceById(id);
        List<Long> idsForRepo = sellerInfo.getProducts().stream().map(SellerProduct::getId).toList();
        List<Long> idsForClient = sellerInfo.getProducts().stream().map(SellerProduct::getProductId).toList();

        productApiClient.removeProducts(idsForClient);
        sellerProductRepository.deleteAllById(idsForRepo);
        sellerInfoRepository.deleteById(id);
        return id;
    }

    private SellerUserDataDto makeUserAsSellerAndGetData(RegisterNewSellerForm form) {
        try {
            SellerUserDataDto sellerData = userDataApiClient.makeUserSeller(form.getUserId()).getBody();
            if (sellerData == null) {
                log.error("Return's created seller user '{}' data is null!", form.getUserId());
                throw new GetSellerDataUserApiClientException(
                        "Return's created seller user '%s' data is null!".formatted(form.getUserId())
                );
            }
            return sellerData;
        } catch (Exception e) {
            log.error("Exception from client while making user as seller! {}", e.getMessage());
            throw new GetSellerDataUserApiClientException(
                    "Exception from client while making user as seller! %s".formatted(e.getMessage())
            );
        }
    }

    private SellerInfo createNewSellerInfo(SellerUserDataDto sellerData) {
        SellerInfo sellerInfo = new SellerInfo();
        sellerInfo.setRating(5.0f);
        sellerInfo.setUserId(sellerData.getId());
        sellerInfo.setRegistrationDate(LocalDateTime.now());
        return sellerInfoRepository.save(sellerInfo);
    }

    private SellerUserDataDto getSellerDataFromAuthorizationService(SellerInfo sellerInfo) {
        try {
            SellerUserDataDto sellerData = userDataApiClient.getSellerInfo(sellerInfo.getUserId()).getBody();
            if (sellerData == null) {
                log.error("Returned seller's '{}' user data is null!", sellerInfo.getUserId());
                throw new GetSellerDataUserApiClientException(
                        "Returned seller's '%s' user data is null!".formatted(sellerInfo.getUserId())
                );
            }
            return sellerData;
        } catch (Exception e) {
            log.error("Exception while getting seller data from client! {}", e.getMessage());
            throw new GetSellerDataUserApiClientException(
                    "Exception while getting seller data from client! %s".formatted(e.getMessage())
            );
        }
    }

    private void checkIfSellerExists(Long sellerId) {
        if (!sellerInfoRepository.existsById(sellerId)) {
            log.warn("Seller with id '{}' not found in repository!", sellerId);
            throw new EntityNotFoundRepositoryException(
                    "Seller with id '%s' not found in repository!".formatted(sellerId)
            );
        }
    }
}
