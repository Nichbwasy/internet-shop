package com.shop.media.service.mapper;

import com.shop.media.dto.metadata.DockMetadataDto;
import com.shop.media.model.MediaElement;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DockMetadataMapper {

    @Mapping(target = "fileExtension", source = "fileExtension.mediaTypeName")
    DockMetadataDto mapToDto(MediaElement from);

}
