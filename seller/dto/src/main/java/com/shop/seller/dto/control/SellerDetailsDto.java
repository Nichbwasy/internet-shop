package com.shop.seller.dto.control;

import com.shop.seller.dto.SellerInfoDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SellerDetailsDto extends SellerInfoDto {

    private Long login;
    private Long email;

}
