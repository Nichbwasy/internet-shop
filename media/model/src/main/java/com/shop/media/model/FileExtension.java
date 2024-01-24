package com.shop.media.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "file_extension")
public class FileExtension {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "File extension name is mandatory!")
    @Size(max = 32, message = "File extension name can't contains more than 32 characters!")
    @Column(name = "name", length = 32, nullable = false, unique = true)
    private String name;

    @NotNull(message = "File extension media type name is mandatory!")
    @Size(max = 32, message = "File extension media type name can't contains more than 32 characters!")
    @Column(name = "media_type_name", length = 32, nullable = false)
    private String mediaTypeName;

}
