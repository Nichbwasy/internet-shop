package com.shop.media.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MediaElementDto {

    private Long id;
    private ProductMediaDto productMedia;
    private String bucketName;
    private String path;
    private String fileName;
    private Long fileSize;
    private FileExtensionDto fileExtension;
    private LocalDateTime creationTime;
    private LocalDateTime lastTimeUpdate;


}
