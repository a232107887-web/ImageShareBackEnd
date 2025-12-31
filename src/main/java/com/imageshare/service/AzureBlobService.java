package com.imageshare.service;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.imageshare.config.AzureStorageConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

@Service
public class AzureBlobService {

    @Autowired
    private AzureStorageConfig azureStorageConfig;

    private BlobContainerClient getContainerClient() {
        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                .connectionString(azureStorageConfig.getConnectionString())
                .buildClient();

        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(
                azureStorageConfig.getContainerName());

        if (!containerClient.exists()) {
            containerClient.create();
        }

        return containerClient;
    }

    public String uploadFile(MultipartFile file) {
        try {
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            BlobContainerClient containerClient = getContainerClient();
            BlobClient blobClient = containerClient.getBlobClient(fileName);

            blobClient.upload(file.getInputStream(), file.getSize(), true);

            return blobClient.getBlobUrl();
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload file to Azure: " + e.getMessage(), e);
        }
    }

    public InputStream downloadFile(String blobUrl) {
        try {
            String blobName = extractBlobNameFromUrl(blobUrl);
            BlobContainerClient containerClient = getContainerClient();
            BlobClient blobClient = containerClient.getBlobClient(blobName);

            return blobClient.openInputStream();
        } catch (Exception e) {
            throw new RuntimeException("Failed to download file from Azure: " + e.getMessage(), e);
        }
    }

    public void deleteFile(String blobUrl) {
        try {
            String blobName = extractBlobNameFromUrl(blobUrl);
            BlobContainerClient containerClient = getContainerClient();
            BlobClient blobClient = containerClient.getBlobClient(blobName);

            blobClient.delete();
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete file from Azure: " + e.getMessage(), e);
        }
    }

    private String extractBlobNameFromUrl(String blobUrl) {
        // Extract blob name from full blob URL
        // URL format: https://account.blob.core.windows.net/container/blobname
        String[] parts = blobUrl.split("/");
        if (parts.length > 0) {
            return parts[parts.length - 1];
        }
        return blobUrl;
    }
}



