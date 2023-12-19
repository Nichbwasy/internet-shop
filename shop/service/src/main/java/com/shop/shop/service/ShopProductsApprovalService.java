package com.shop.shop.service;

import com.shop.product.dto.form.product.ApprovalStatusProductFilterForm;
import com.shop.product.dto.form.product.ChangeProductApprovalStatusForm;
import com.shop.shop.dto.shop.ShopPageProductInfoDto;

import java.util.List;

public interface ShopProductsApprovalService {

    List<ShopPageProductInfoDto> showProductsPage(Integer page, ApprovalStatusProductFilterForm form);
    ShopPageProductInfoDto showProductInfo(Long id);
    ShopPageProductInfoDto changeProductInfo(ChangeProductApprovalStatusForm form);
}
