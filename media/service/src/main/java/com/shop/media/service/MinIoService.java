package com.shop.media.service;

import com.shop.media.dto.GetFileForm;
import com.shop.media.dto.RemoveFileForm;
import com.shop.media.dto.UploadFileForm;

import java.io.InputStream;

public interface MinIoService {

    InputStream getFile(GetFileForm form);
    String uploadFile(UploadFileForm form);
    String removeFile(RemoveFileForm form);

}
