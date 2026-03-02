package com.dms.app.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
public class S3StorageService {

    public static class S3ObjectData {
        private final byte[] bytes;
        private final String contentType;

        public S3ObjectData(byte[] bytes, String contentType) {
            this.bytes = bytes;
            this.contentType = contentType;
        }

        public byte[] getBytes() {
            return bytes;
        }

        public String getContentType() {
            return contentType;
        }
    }

    private final S3Client s3Client;

    @Value("${app.s3.bucket-name}")
    private String bucketName;

    @Value("${app.s3.prefix:realestate-app}")
    private String prefix;

    @Value("${cloud.aws.region}")
    private String region;

    @Value("${app.s3.public-base-url:}")
    private String publicBaseUrl;

    public S3StorageService(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public String uploadFile(String key, MultipartFile file) {
        try {
            String normalizedKey = prefixedKey(key);
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(requireBucketName())
                    .key(normalizedKey)
                    .contentType(file.getContentType())
                    .build();
            s3Client.putObject(request, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
            return normalizedKey;
        } catch (Exception e) {
            throw new RuntimeException("Error uploading file to S3", e);
        }
    }

    public String uploadBytes(String key, byte[] bytes, String contentType) {
        try {
            String normalizedKey = prefixedKey(key);
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(requireBucketName())
                    .key(normalizedKey)
                    .contentType(contentType)
                    .build();
            s3Client.putObject(request, RequestBody.fromBytes(bytes));
            return normalizedKey;
        } catch (Exception e) {
            throw new RuntimeException("Error uploading bytes to S3", e);
        }
    }

    public String uploadLocalFile(String key, File file, String contentType) {
        try {
            byte[] bytes = Files.readAllBytes(file.toPath());
            return uploadBytes(key, bytes, contentType);
        } catch (IOException e) {
            throw new RuntimeException("Error reading local file for S3 upload", e);
        }
    }

    public S3ObjectData downloadFile(String key) {
        try {
            GetObjectRequest request = GetObjectRequest.builder()
                    .bucket(requireBucketName())
                    .key(prefixedKey(key))
                    .build();
            ResponseBytes<GetObjectResponse> response = s3Client.getObjectAsBytes(request);
            return new S3ObjectData(response.asByteArray(), response.response().contentType());
        } catch (Exception e) {
            throw new RuntimeException("Error downloading file from S3: " + key, e);
        }
    }

    public void downloadToFile(String key, File targetFile) {
        try {
            S3ObjectData data = downloadFile(key);
            Path parent = targetFile.toPath().getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Files.write(targetFile.toPath(), data.getBytes());
        } catch (Exception e) {
            throw new RuntimeException("Error downloading S3 object to file: " + key, e);
        }
    }

    public void deleteFile(String key) {
        try {
            DeleteObjectRequest request = DeleteObjectRequest.builder()
                    .bucket(requireBucketName())
                    .key(prefixedKey(key))
                    .build();
            s3Client.deleteObject(request);
        } catch (Exception e) {
            throw new RuntimeException("Error deleting file from S3: " + key, e);
        }
    }

    public String buildPublicBaseUrl() {
        String base = hasText(publicBaseUrl)
                ? trimTrailingSlash(publicBaseUrl)
                : "https://" + requireBucketName() + ".s3." + region + ".amazonaws.com";

        if (hasText(prefix)) {
            return base + "/" + trimSlashes(prefix) + "/";
        }
        return base + "/";
    }

    public String buildPublicObjectUrl(String key) {
        return buildPublicBaseUrl() + normalizeKey(key);
    }

    public String resolveKey(String key) {
        return prefixedKey(key);
    }

    private String prefixedKey(String key) {
        String normalized = normalizeKey(key);
        String normalizedPrefix = trimSlashes(prefix);
        if (!hasText(normalizedPrefix)) {
            return normalized;
        }
        if (normalized.equals(normalizedPrefix) || normalized.startsWith(normalizedPrefix + "/")) {
            return normalized;
        }
        if (!hasText(normalized)) {
            return normalizedPrefix;
        }
        return normalizedPrefix + "/" + normalized;
    }

    private String normalizeKey(String key) {
        if (key == null) {
            return "";
        }
        String normalized = key.replace("\\", "/").trim();
        while (normalized.startsWith("/")) {
            normalized = normalized.substring(1);
        }
        return normalized;
    }

    private String trimTrailingSlash(String value) {
        String result = value;
        while (result.endsWith("/")) {
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }

    private String trimSlashes(String value) {
        if (value == null) {
            return "";
        }
        String result = value.trim();
        while (result.startsWith("/")) {
            result = result.substring(1);
        }
        while (result.endsWith("/")) {
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }

    private String requireBucketName() {
        if (!hasText(bucketName)) {
            throw new IllegalStateException("S3 bucket name is not configured. Set S3_BUCKET_NAME.");
        }
        return bucketName;
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
