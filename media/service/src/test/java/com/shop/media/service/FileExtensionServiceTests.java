package com.shop.media.service;

import com.shop.common.utils.all.exception.dao.EntityNotFoundRepositoryException;
import com.shop.media.common.data.builder.FileExtensionBuilder;
import com.shop.media.common.data.builder.FileExtensionDtoBuilder;
import com.shop.media.dao.FileExtensionRepository;
import com.shop.media.dto.FileExtensionDto;
import com.shop.media.model.FileExtension;
import com.shop.media.service.config.FileExtensionServiceTestConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {FileExtensionServiceTestConfiguration.class})
public class FileExtensionServiceTests {

    @Autowired
    private FileExtensionRepository fileExtensionRepository;
    @Autowired
    private FileExtensionService fileExtensionService;

    @Test
    public void getAllFileExtensionsTest() {
        Mockito.when(fileExtensionRepository.findAll())
                .thenReturn(List.of(
                        FileExtensionBuilder.fileExtension().build(),
                        FileExtensionBuilder.fileExtension().build()
                ));

        List<FileExtensionDto> result = fileExtensionService.getAllFileExtensions();

        Assertions.assertEquals(2, result.size());
    }

    @Test
    public void getAllFileExtensionsRepositoryExceptionTest() {
        Mockito.when(fileExtensionRepository.findAll()).thenThrow(RuntimeException.class);

        Assertions.assertThrows(RuntimeException.class, () -> fileExtensionService.getAllFileExtensions());
    }

    @Test
    public void getFileExtensionTest() {
        FileExtension fileExtension = FileExtensionBuilder.fileExtension().build();

        Mockito.when(fileExtensionRepository.findById(fileExtension.getId())).thenReturn(Optional.of(fileExtension));

        FileExtensionDto result = fileExtensionService.getFileExtension(fileExtension.getId());

        Assertions.assertEquals(fileExtension.getId(), result.getId());
        Assertions.assertEquals(fileExtension.getName(), result.getName());
        Assertions.assertEquals(fileExtension.getMediaTypeName(), result.getMediaTypeName());
    }

    @Test
    public void getFileExtensionNotFoundTest() {
        Mockito.when(fileExtensionRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundRepositoryException.class,
                () -> fileExtensionService.getFileExtension(1L));
    }

    @Test
    public void getFileExtensionRepositoryExceptionTest() {
        Mockito.when(fileExtensionRepository.findById(Mockito.anyLong())).thenThrow(RuntimeException.class);

        Assertions.assertThrows(RuntimeException.class,
                () -> fileExtensionService.getFileExtension(1L));
    }

    @Test
    public void getFileExtensionNullDataTest() {
        Mockito.when(fileExtensionRepository.findById(Mockito.nullable(Long.class))).thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundRepositoryException.class,
                () -> fileExtensionService.getFileExtension(null));
    }

    @Test
    public void addFileExtensionTest() {
        FileExtensionDto fileExtensionDto = FileExtensionDtoBuilder.fileExtensionDto().id(null).build();

        Mockito.when(fileExtensionRepository.save(Mockito.any(FileExtension.class)))
                .thenAnswer(a -> {
                    FileExtension fileExtension = a.getArgument(0);
                    fileExtension.setId(1L);
                    return fileExtension;
                });

        FileExtensionDto result = fileExtensionService.addFileExtension(fileExtensionDto);
        Assertions.assertEquals(1L, result.getId());
        Assertions.assertEquals(fileExtensionDto.getName(), result.getName());
        Assertions.assertEquals(fileExtensionDto.getMediaTypeName(), result.getMediaTypeName());
    }

    @Test
    public void addFileExtensionRepositoryExceptionTest() {
        FileExtensionDto fileExtensionDto = FileExtensionDtoBuilder.fileExtensionDto().id(null).build();

        Mockito.when(fileExtensionRepository.save(Mockito.any(FileExtension.class))).thenThrow(RuntimeException.class);

        Assertions.assertThrows(RuntimeException.class, () -> fileExtensionService.addFileExtension(fileExtensionDto));
    }

    @Test
    public void addFileExtensionNullTest() {
        Mockito.when(fileExtensionRepository.save(Mockito.nullable(FileExtension.class)))
                .thenThrow(IllegalArgumentException.class);

        Assertions.assertThrows(NullPointerException.class, () -> fileExtensionService.addFileExtension(null));
    }

    @Test
    public void updateFileExtensionTest() {
        FileExtensionDto fileExtensionDto = FileExtensionDtoBuilder.fileExtensionDto().id(1L).build();
        FileExtension fileExtension = FileExtensionBuilder.fileExtension().id(1L).build();

        Mockito.when(fileExtensionRepository.findById(fileExtension.getId())).thenReturn(Optional.of(fileExtension));

        FileExtensionDto result = fileExtensionService.updateFileExtension(fileExtensionDto);

        Assertions.assertEquals(fileExtensionDto, result);
    }

    @Test
    public void updateNotExistedFileExtensionTest() {
        FileExtensionDto fileExtensionDto = FileExtensionDtoBuilder.fileExtensionDto().build();

        Mockito.when(fileExtensionRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundRepositoryException.class,
                () -> fileExtensionService.updateFileExtension(fileExtensionDto));
    }

    @Test
    public void updateFileExtensionNullDataTest() {
        FileExtensionDto fileExtensionDto = new FileExtensionDto();

        Mockito.when(fileExtensionRepository.findById(Mockito.nullable(Long.class))).thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundRepositoryException.class,
                () -> fileExtensionService.updateFileExtension(fileExtensionDto));
    }

    @Test
    public void updateFileExtensionNullTest() {
        Mockito.when(fileExtensionRepository.findById(Mockito.nullable(Long.class))).thenReturn(Optional.empty());

        Assertions.assertThrows(NullPointerException.class,
                () -> fileExtensionService.updateFileExtension(null));
    }

    @Test
    public void removeFileExtensionTest() {
        Mockito.doNothing().when(fileExtensionRepository).deleteById(Mockito.anyLong());

        Assertions.assertEquals(1L, fileExtensionService.removeFileExtension(1L));
    }

    @Test
    public void removeFileExtensionNullTest() {
        Mockito.doThrow(IllegalArgumentException.class).when(fileExtensionRepository).deleteById(Mockito.nullable(Long.class));

        Assertions.assertThrows(IllegalArgumentException.class, () -> fileExtensionService.removeFileExtension(null));
    }

    @Test
    public void removeFileExtensionRepositoryExceptionTest() {
        Mockito.doThrow(RuntimeException.class).when(fileExtensionRepository).deleteById(Mockito.anyLong());

        Assertions.assertThrows(RuntimeException.class, () -> fileExtensionService.removeFileExtension(1L));
    }

}
