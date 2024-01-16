package com.example.test;

import java.io.IOException;
import java.io.InputStream;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
public class TestController {
    static final String KEY = "AmazonS3TEST";
    private final Bucket bucket;
    private final AmazonS3 s3Client;

    @ExceptionHandler(IOException.class)
    public ResponseEntity<?> handleIOException(IOException ioException) {
        String msg = ioException.getLocalizedMessage();
        log.error(msg);
        return ResponseEntity.badRequest().body(msg);
    }

    @ExceptionHandler(AmazonServiceException.class)
    public ResponseEntity<?> handleAmazonServiceException(AmazonServiceException amazonServiceException) {
        String msg = amazonServiceException.getLocalizedMessage();
        log.error(msg);
        return ResponseEntity.badRequest().body(msg);
    }

    @GetMapping
    public ResponseEntity<?> getImage(@RequestParam(name = "key", required = false, defaultValue = KEY) String key) {
        log.info("Key: " + key);
        GetObjectRequest getObjectRequest = new GetObjectRequest(bucket.getName(), key);

        S3Object s3Object = s3Client.getObject(getObjectRequest);
        log.info(String.valueOf(s3Object));

        return ResponseEntity.ok().body(s3Object);
    }

    @PostMapping
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) throws IOException, AmazonServiceException {
        InputStream fileInputStream = file.getInputStream();
        String contentType = file.getContentType();
        String originalFileName = file.getOriginalFilename();
        Long size = file.getSize();
        log.info("ContentType: " + contentType);
        log.info("OriginalFileName: " + originalFileName);
        log.info("Size: " + size);

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(contentType);
        metadata.setContentLength(size);

        PutObjectRequest putObjectRequest = new PutObjectRequest(bucket.getName(), KEY, fileInputStream, metadata);

        s3Client.putObject(putObjectRequest);

        return ResponseEntity.ok().body("Successful upload");
    }

    @DeleteMapping
    public ResponseEntity<?> deleteImage(
            @RequestParam(name = "key", required = false, defaultValue = KEY) String key) throws AmazonServiceException {
        log.info("Key: " + key);
        DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(bucket.getName(), key);

        s3Client.deleteObject(deleteObjectRequest);

        return ResponseEntity.ok().body("Successful delete");
    }
}
