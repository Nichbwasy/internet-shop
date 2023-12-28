package com.shop.media.dto;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MediaElementDto {

    private Long id;
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonBackReference
    private ProductMediaDto productMedia;
    private String bucketName;
    private String path;
    private String fileName;
    private Long fileSize;
    private FileExtensionDto fileExtension;
    private LocalDateTime creationTime;
    private LocalDateTime lastTimeUpdate;


}
