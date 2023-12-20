package com.shop.media.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UploadFileForm {

    private String fileName;
    private String bucketName;
    private MultipartFile multipartFile;


}
