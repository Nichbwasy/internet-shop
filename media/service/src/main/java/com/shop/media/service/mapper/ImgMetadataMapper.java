package com.shop.media.service.mapper;

import com.shop.media.dto.metadata.ImgMetadataDto;
import com.shop.media.model.MediaElement;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ImgMetadataMapper {

    @Mapping(target = "fileExtension", source = "fileExtension.mediaTypeName")
    ImgMetadataDto mapToDto(MediaElement model);

}
