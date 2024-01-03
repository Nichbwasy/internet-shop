package com.shop.media.client;

import com.shop.media.dto.form.GetFileForm;
import com.shop.media.dto.form.RemoveFileForm;
import com.shop.media.dto.form.UploadFileForm;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.core.io.InputStreamResource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(name = "minIoApiClient", url = "${microservice.media.url}", path = "/api/media/minio")
public interface MinIoApiClient {

    @GetMapping("/{bucket}")
    InputStreamResource getFile(@RequestBody GetFileForm form, @PathVariable("bucket") String bucket);
    @PostMapping("/{bucket}")
    String uploadFile(@ModelAttribute UploadFileForm form,
                      @ModelAttribute MultipartFile file,
                      @PathVariable("bucket") String bucket);
    @DeleteMapping("/{bucket}")
    String removeFile(@RequestBody RemoveFileForm form, @PathVariable("bucket") String bucket);

}
