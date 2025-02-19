package com.books.spring.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.Duration;
import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;

@Service
public class S3Service {

  @Autowired
  private AmazonS3 s3Client;

  @Value("${aws.s3.bucket}")
  private String bucketName;

  public String uploadFile(MultipartFile file) {
    try {
      String fileName = generateFileName(file);
      ObjectMetadata metadata = new ObjectMetadata();
      metadata.setContentType(file.getContentType());
      metadata.setContentLength(file.getSize());
      s3Client.putObject(new PutObjectRequest(bucketName, fileName, file.getInputStream(), metadata)
          .withCannedAcl(CannedAccessControlList.PublicRead));

      return fileName;
    } catch (IOException e) {
      throw new RuntimeException("Failed to upload file", e);
    }
  }

  public String getFileUrl(String fileName) {
    return s3Client.getUrl(bucketName, fileName).toString();
  }

  public void deleteFile(String fileName) {
    s3Client.deleteObject(bucketName, fileName);
  }

  public URL generatePresignedUrl(String fileName, HttpMethod httpMethod, Duration expiration) {
    GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucketName, fileName)
        .withMethod(httpMethod)
        .withExpiration(new Date(System.currentTimeMillis() + expiration.toMillis()));

    return s3Client.generatePresignedUrl(request);
  }

  private String generateFileName(MultipartFile file) {
    return UUID.randomUUID().toString() + "-" + file.getOriginalFilename().replace(" ", "_");
  }
}