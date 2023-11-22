package com.shop.product.dto.form;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddOrRemoveForm {

    private Long targetId;
    private List<Long> addedOrRemovedIds;

}
