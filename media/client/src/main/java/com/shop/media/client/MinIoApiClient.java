package com.shop.media.client;

import com.shop.media.dto.GetFileForm;
import com.shop.media.dto.RemoveFileForm;
import com.shop.media.dto.UploadFileForm;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.core.io.InputStreamResource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(name = "minIoApiClient", url = "${microservice.product.url}", path = "/api/media/minio")
public interface MinIoApiClient {

    @GetMapping("/{bucket}")
    InputStreamResource getFile(@RequestBody GetFileForm form, @PathVariable String bucket);
    @PostMapping("/{bucket}")
    String uploadFile(@ModelAttribute UploadFileForm form, @ModelAttribute MultipartFile file, @PathVariable String bucket);
    @DeleteMapping("/{bucket}")
    String removeFile(@RequestBody RemoveFileForm form, @PathVariable String bucket);

}
