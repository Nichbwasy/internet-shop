package com.shop.media.dao.impl;

import com.shop.media.dao.MinIoStorage;
import com.shop.media.dao.exception.MinIoFileGetException;
import com.shop.media.dao.exception.MinIoFileRemoveException;
import com.shop.media.dao.exception.MinIoFileUploadException;
import com.shop.media.dao.exception.MinIoStorageException;
import io.minio.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Slf4j
@Component
@RequiredArgsConstructor
public class MinIoStorageImpl implements MinIoStorage {

    private final MinioClient minioClient;

    @Override
    public Boolean bucketCheck(String bucketName) {
        try {
            return minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        } catch (Exception e) {
            log.error("Exception while checking minio bucket existence! {}", e.getMessage());
            throw new MinIoStorageException("Exception while checking minio bucket existence! %s".formatted(e.getMessage()));
        }
    }

    @Override
    public InputStream getFile(String fileName, String bucket) {
        try {
            log.info("Getting file '{}' from the bucket '{}'...", fileName, bucket);
            GetObjectResponse response = minioClient.getObject(GetObjectArgs.builder()
                    .bucket(bucket)
                    .object(fileName)
                    .build());
            log.info("File '{}' has been found in the bucket '{}'.", response.object(), response.bucket());
            return response;
        } catch (Exception e) {
            log.error("Unable to get file '{}' from the bucket '{}'! {}", fileName, bucket, e.getMessage());
            throw new MinIoFileGetException(
                    "Unable to get file '%s' from the bucket '%s'! %s".formatted(fileName, bucket, e.getMessage())
            );
        }
    }

    @Override
    public void uploadFile(String fileName, String bucket, InputStream data, Long dataSize, String contentType) {
        try {
            log.info("Saving file's input stream to the bucket '{}'...", bucket);
            ObjectWriteResponse response = minioClient.putObject(PutObjectArgs.builder()
                    .object(fileName)
                    .bucket(bucket)
                    .stream(data, dataSize, 0)
                    .contentType(contentType)
                    .build());
            log.info("File '{}' has been saved into bucket '{}' successfully.", response.object(), response.bucket());
        } catch (Exception e) {
            log.error("Unable to save file input stream to the minio bucket '{}'! {}", bucket, e.getMessage());
            throw new MinIoFileUploadException(
                    "Unable to save file input stream to the minio bucket '%s'! %s".formatted(bucket, e.getMessage())
            );
        }
    }

    @Override
    public void removeFile(String fileName, String bucket) {
        try {
            log.info("Removing file '{}' from the bucket '{}'...", fileName, bucket);
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .object(fileName)
                    .bucket(bucket)
                    .build());
            log.info("File '{}' has been removed successfully from the bucket '{}'.", fileName, bucket);
        } catch (Exception e) {
            log.error("Unable remove file '{}' from the bucket '{}'! {}", fileName, bucket, e.getMessage());
            throw new MinIoFileRemoveException(
                    "Unable remove file '%s' from the bucket '%s'! %s".formatted(fileName, bucket, e.getMessage())
            );
        }
    }
}
