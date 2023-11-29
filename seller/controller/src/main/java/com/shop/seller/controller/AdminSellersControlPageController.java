package com.shop.seller.controller;

import com.shop.seller.dto.SellerInfoDto;
import com.shop.seller.dto.control.RegisterNewSellerForm;
import com.shop.seller.dto.control.SellerDetailsDto;
import com.shop.seller.service.AdminSellersControlService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admin/control/sellers")
@RequiredArgsConstructor
public class AdminSellersControlPageController {

    private final AdminSellersControlService sellersService;

    @GetMapping("/{page}")
    public ResponseEntity<List<SellerInfoDto>> getSellersPage(@PathVariable Integer page) {
        log.info("Trying to get sellers for the page '{}'...", page);
        return ResponseEntity.ok().body(sellersService.getSellersInfoFromPage(page));
    }

    @GetMapping("/seller/{id}")
    public ResponseEntity<SellerInfoDto> getSellerInfo(@PathVariable Long id) {
        log.info("Trying to get info about seller with id '{}'...", id);
        return ResponseEntity.ok().body(sellersService.getSellerInfo(id));
    }

    @PostMapping("/new")
    public ResponseEntity<SellerDetailsDto> registerUserAsSeller(@RequestBody RegisterNewSellerForm form) {
        log.info("Trying to register user '{}' as a seller...", form.getUserId());
        return ResponseEntity.ok().body(sellersService.registerNewSeller(form));
    }

    @DeleteMapping("/seller/{id}")
    public ResponseEntity<List<Long>> removeSellerFromSystem(@PathVariable Long id) {
        log.info("Trying to remove seller from '{}' from the system...", id);
        return ResponseEntity.ok().body(sellersService.removeSellerFromSystem(id));
    }

}
