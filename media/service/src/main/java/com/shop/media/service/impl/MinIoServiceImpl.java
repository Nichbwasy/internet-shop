package com.shop.media.service.impl;

import com.shop.common.utils.all.file.FilesUtils;
import com.shop.media.dao.storage.MinIoStorage;
import com.shop.media.dto.GetFileForm;
import com.shop.media.dto.RemoveFileForm;
import com.shop.media.dto.UploadFileForm;
import com.shop.media.service.MinIoService;
import com.shop.media.service.exeption.FileUploadingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class MinIoServiceImpl implements MinIoService {

    private final MinIoStorage minIoStorage;

    @Override
    public InputStream getFile(@Valid GetFileForm form) {
        return minIoStorage.getFile(form.getFileName(), form.getBucketName());
    }

    @Override
    public String uploadFile(UploadFileForm form) {
        try {
            long size = form.getMultipartFile().getSize();
            String contentType = form.getMultipartFile().getContentType();
            InputStream data = form.getMultipartFile().getInputStream();
            String originalFilename = form.getMultipartFile().getOriginalFilename();
            String fileExtension = FilesUtils.extractFileExtension(originalFilename);
            String newFileName = form.getFileName() + fileExtension;
            minIoStorage.uploadFile(newFileName, form.getBucketName(), data, size, contentType);
            return newFileName;
        } catch (IOException e) {
            log.error("Unable upload file! {}", e.getMessage());
            throw new FileUploadingException(("Unable upload file! %s".formatted(e.getMessage())));
        }
    }

    @Override
    public String removeFile(RemoveFileForm form) {
        minIoStorage.removeFile(form.getFileName(), form.getBucketName());
        return form.getFileName();
    }
}
