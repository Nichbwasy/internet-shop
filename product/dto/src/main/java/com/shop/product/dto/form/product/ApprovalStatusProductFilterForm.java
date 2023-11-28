package com.shop.product.dto.form.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ApprovalStatusProductFilterForm extends ProductFilterForm {

    private Boolean showCreated = false;
    private Boolean showApproved = false;
    private Boolean showUnapproved = false;
    private Boolean showBanned = false;

}
