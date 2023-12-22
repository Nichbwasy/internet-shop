package com.shop.media.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductMediaDto {

    private Long id;
    private Long productId;
    private List<MediaElementDto> mediaElements;

}
