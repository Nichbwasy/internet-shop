package com.shop.media.dao;

import java.io.InputStream;

public interface MinIoStorage {

    Boolean bucketCheck(String bucketName);
    InputStream getFile(String fileName, String bucket);
    void uploadFile(String fileName, String bucket, InputStream data, Long dataSize, String contentType);
    void removeFile(String fileName, String bucket);

}
