package com.shop.media.service;

import com.shop.media.common.data.builder.RemoveFileFormBuilder;
import com.shop.media.common.data.builder.UploadFileFormBuilder;
import com.shop.media.dao.MinIoStorage;
import com.shop.media.dao.exception.MinIoFileRemoveException;
import com.shop.media.dao.exception.MinIoFileUploadException;
import com.shop.media.dto.RemoveFileForm;
import com.shop.media.dto.UploadFileForm;
import com.shop.media.service.config.MinIoServiceTestConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {MinIoServiceTestConfiguration.class})
public class MinIoServiceTests {

    @Autowired
    private MinIoStorage minIoStorage;
    @Autowired
    private MinIoService minIoService;

    @Test
    public void uploadFileTest() throws Exception {
        MultipartFile fileMock = Mockito.mock(MultipartFile.class);
        UploadFileForm form = UploadFileFormBuilder.uploadFileForm()
                .multipartFile(fileMock)
                .build();

        Mockito.when(fileMock.getContentType()).thenReturn("image/jpeg");
        Mockito.when(fileMock.getInputStream()).thenReturn(null);
        Mockito.when(fileMock.getSize()).thenReturn(1028L);
        Mockito.when(fileMock.getOriginalFilename()).thenReturn("some_img.jpg");
        Mockito.doNothing().when(minIoStorage)
                .uploadFile(Mockito.anyString(), Mockito.anyString(), Mockito.nullable(InputStream.class),
                        Mockito.anyLong(), Mockito.anyString());

        String result = minIoService.uploadFile(form);

        Assertions.assertEquals(form.getFileName() + ".jpg", result);
    }

    @Test
    public void uploadFileMinIoExceptionTest() throws Exception {
        MultipartFile fileMock = Mockito.mock(MultipartFile.class);
        UploadFileForm form = UploadFileFormBuilder.uploadFileForm()
                .multipartFile(fileMock)
                .build();

        Mockito.when(fileMock.getContentType()).thenReturn("image/jpeg");
        Mockito.when(fileMock.getInputStream()).thenReturn(null);
        Mockito.when(fileMock.getSize()).thenReturn(1028L);
        Mockito.when(fileMock.getOriginalFilename()).thenReturn("some_img.jpg");
        Mockito.doThrow(MinIoFileUploadException.class).when(minIoStorage)
                .uploadFile(Mockito.anyString(), Mockito.anyString(), Mockito.nullable(InputStream.class),
                        Mockito.anyLong(), Mockito.anyString());

        Assertions.assertThrows(MinIoFileUploadException.class, () -> minIoService.uploadFile(form));
    }

    @Test
    public void uploadFileNullDataTest() {
        UploadFileForm form = new UploadFileForm();

        Assertions.assertThrows(NullPointerException.class, () -> minIoService.uploadFile(form));
    }

    @Test
    public void uploadFileNullFormTest() {
        Assertions.assertThrows(NullPointerException.class, () -> minIoService.uploadFile(null));
    }

    @Test
    public void removeFileTest() {
        RemoveFileForm form = RemoveFileFormBuilder.removeFileForm().build();
        Mockito.doNothing().when(minIoStorage).removeFile(Mockito.anyString(), Mockito.anyString());

        Assertions.assertEquals(form.getFileName(), minIoService.removeFile(form));
    }

    @Test
    public void removeFileMinioExceptionTest() {
        RemoveFileForm form = RemoveFileFormBuilder.removeFileForm().build();
        Mockito.doThrow(MinIoFileRemoveException.class).when(minIoStorage).removeFile(Mockito.anyString(), Mockito.anyString());

        Assertions.assertThrows(MinIoFileRemoveException.class, () -> minIoService.removeFile(form));
    }

}
