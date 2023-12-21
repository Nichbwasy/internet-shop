package com.shop.media.run.config;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class MinIoBucketsInitialization {

    @Value("${minio.bucket.names}")
    private List<String> bucketNames;

    private final MinioClient minioClient;

    @EventListener
    public void initBuckets(ContextRefreshedEvent event) throws Exception {
        log.info("Context has been build. Initializing buckets in MinIO container...");
        for (String name: bucketNames) {
            log.info("Trying to create bucket '{}'...", name);
            if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(name).build())) {
                minioClient.makeBucket(MakeBucketArgs.builder()
                        .bucket(name)
                        .build());
                log.info("Bucket '{}' has been created successfully.", name);
            } else {
                log.info("Bucket '{}' already exists. Skip.", name);
            }
        }
        log.info("Buckets initialization has been finished.");
    }

}
