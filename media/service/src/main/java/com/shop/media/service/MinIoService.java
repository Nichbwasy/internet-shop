package com.shop.media.service;

import com.shop.media.dto.form.GetFileForm;
import com.shop.media.dto.form.RemoveFileForm;
import com.shop.media.dto.form.UploadFileForm;

import java.io.InputStream;

public interface MinIoService {

    InputStream getFile(GetFileForm form);
    String uploadFile(UploadFileForm form);
    String removeFile(RemoveFileForm form);

}
