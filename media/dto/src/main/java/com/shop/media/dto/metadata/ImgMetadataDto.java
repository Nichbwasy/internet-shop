package com.shop.media.dto.metadata;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImgMetadataDto {

    private String fileName;
    private Long fileSize;
    private String fileExtension;
    private LocalDateTime creationTime;
    private LocalDateTime lastTimeUpdate;

}
