package com.shop.media.service.mapper;

import com.shop.common.utils.all.mapping.CommonCrudMapper;
import com.shop.media.dto.FileExtensionDto;
import com.shop.media.model.FileExtension;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FileExtensionMapper extends CommonCrudMapper<FileExtension, FileExtensionDto> {

}
