package com.mu.musmart.service;

import io.minio.*;
import io.minio.http.Method;
import io.minio.messages.Bucket;
import io.minio.messages.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class MinioService {

    private static final Logger log = LoggerFactory.getLogger(MinioService.class);
    @Autowired
    private MinioClient minioClient;

    @Value("${minio.bucket-name}")
    private String bucketName;

    /**
     * 创建桶
     */
    public void createBucket(String bucketName) throws Exception {
        boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        if (!exists) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        }
    }

    /**
     * 上传文件
     */
    public String uploadFile(MultipartFile file) throws Exception {
        // 确保桶存在
        createBucket(bucketName);

        // 生成文件名
        String fileName = System.currentTimeMillis() + "-" + file.getOriginalFilename();

        // 上传文件
        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(fileName)
                        .stream(file.getInputStream(), file.getSize(), -1)
                        .contentType(file.getContentType())
                        .build()
        );
        log.info("文件上传成功，文件名：{}", fileName);
        String fileUrl = getFileUrl(fileName);
        log.info("文件下载URL：{}", fileUrl);

        return fileUrl;
    }

    /**
     * 下载文件
     */
    public InputStream downloadFile(String fileName) throws Exception {
        return minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucketName)
                        .object(fileName)
                        .build()
        );
    }

    /**
     * 删除文件
     */
    public void deleteFile(String fileName) throws Exception {
        minioClient.removeObject(
                RemoveObjectArgs.builder()
                        .bucket(bucketName)
                        .object(fileName)
                        .build()
        );
    }

    /**
     * 获取文件URL(带过期时间)
     */
    public String getFileUrl(String fileName, int expires) throws Exception {
        return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .bucket(bucketName)
                        .object(fileName)
                        .method(Method.GET)
                        .expiry(expires, TimeUnit.SECONDS)
                        .build()
        );
    }

    /**
     * 获取文件URL
     */
    public String getFileUrl(String fileName) throws Exception {
        return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .bucket(bucketName)
                        .object(fileName)
                        .method(Method.GET)
                        .build()
        );
    }

    /**
     * 列出所有桶
     */
    public List<Bucket> listBuckets() throws Exception {
        return minioClient.listBuckets();
    }

    /**
     * 列出桶中的所有文件
     */
    public List<String> listObjects(String bucketName) throws Exception {
        List<String> objectNames = new ArrayList<>();
        Iterable<Result<Item>> results = minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket(bucketName)
                        .build()
        );
        for (Result<Item> result : results) {
            Item item = result.get();
            objectNames.add(item.objectName());
        }
        return objectNames;
    }
}
