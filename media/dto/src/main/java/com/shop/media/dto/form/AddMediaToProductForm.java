package com.shop.media.dto.form;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddMediaToProductForm {

    private Long productMediaId;
    private MultipartFile multipartFile;

}
