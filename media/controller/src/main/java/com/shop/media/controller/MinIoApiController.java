package com.shop.media.controller;

import com.shop.media.dto.GetFileForm;
import com.shop.media.dto.RemoveFileForm;
import com.shop.media.dto.UploadFileForm;
import com.shop.media.service.MinIoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/media/minio")
public class MinIoApiController {

    private final MinIoService minIoService;

    @GetMapping("/{bucket}")
    public InputStreamResource getFile(@RequestBody GetFileForm form, @PathVariable String bucket) {
        log.info("Trying to get file '{}' from '{}' minio bucket...", form.getFileName(), bucket);
        form.setBucketName(bucket);
        return new InputStreamResource(minIoService.getFile(form));
    }

    @PostMapping("/{bucket}")
    public String uploadFile(@ModelAttribute UploadFileForm form,
                             @ModelAttribute MultipartFile file,
                             @PathVariable String bucket) {
        log.info("Trying to save file '{}' to the '{}' minio bucket...", form.getFileName(), form.getBucketName());
        form.setBucketName(bucket);
        form.setMultipartFile(file);
        return minIoService.uploadFile(form);
    }

    @DeleteMapping("/{bucket}")
    public String removeFile(@RequestBody RemoveFileForm form, @PathVariable String bucket) {
        log.info("Trying to remove file '{}' from the '{}' minio bucket...", form.getFileName(), bucket);
        form.setBucketName(bucket);
        return minIoService.removeFile(form);
    }

}
