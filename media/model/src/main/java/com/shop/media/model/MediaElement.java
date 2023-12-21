package com.shop.media.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "media_element")
public class MediaElement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @EqualsAndHashCode.Exclude
    @ManyToOne(targetEntity = ProductMedia.class, fetch = FetchType.LAZY)
    private ProductMedia productMedia;

    @NotNull(message = "Bucket name is mandatory!")
    @Size(min = 3, max = 128, message = " Bucket name must contains from 3 to 128 characters!")
    @Column(name = "bucket_name", length = 128, nullable = false)
    private String bucketName;

    @NotNull(message = "File path is mandatory!")
    @Size(max = 256, message = " Bucket name must contains no more than 256 characters!")
    @Column(name = "path", length = 256, nullable = false)
    private String path;

    @NotNull(message = "File name is mandatory!")
    @Size(min = 32, max = 32, message = " Bucket name must contains exactly 32 characters!")
    @Column(name = "file_name", length = 32, nullable = false)
    private String fileName;

    @NotNull(message = "File size in bytes is mandatory!")
    @Min(value = 0, message = "File size can't be negative!")
    @Column(name = "file_size", nullable = false, columnDefinition = "0")
    private Long fileSize;

    @EqualsAndHashCode.Exclude
    @OneToOne(targetEntity = FileExtension.class)
    private FileExtension fileExtension;

    @NotNull(message = "Created time is mandatory!")
    @Column(name = "creation_time", nullable = false)
    private LocalDateTime creationTime;

    @NotNull(message = "Last time update is mandatory!")
    @Column(name = "last_time_update", nullable = false)
    private LocalDateTime lastTimeUpdate;

}
