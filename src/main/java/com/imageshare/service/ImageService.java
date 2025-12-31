package com.imageshare.service;

import com.imageshare.model.Image;
import com.imageshare.model.User;
import com.imageshare.repository.ImageRepository;
import com.imageshare.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ImageService {

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AzureBlobService azureBlobService;

    public Image uploadImage(MultipartFile file, String userId, String imageName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String blobUrl = azureBlobService.uploadFile(file);

        Image image = new Image();
        image.setName(imageName != null ? imageName : file.getOriginalFilename());
        image.setUserId(userId);
        image.setAuthorName(user.getUsername());
        image.setBlobUrl(blobUrl);
        image.setContentType(file.getContentType());
        image.setFileSize(file.getSize());
        image.setUploadDate(LocalDateTime.now());
        image.setUpdatedAt(LocalDateTime.now());

        return imageRepository.save(image);
    }

    public List<Image> getAllImages() {
        return imageRepository.findAllByOrderByUploadDateDesc();
    }

    public List<Image> getUserImages(String userId) {
        return imageRepository.findByUserIdOrderByUploadDateDesc(userId);
    }

    public Image updateImageName(String imageId, String userId, String newName) {
        Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Image not found"));

        if (!image.getUserId().equals(userId)) {
            throw new RuntimeException("No permission to modify this image");
        }

        image.setName(newName);
        image.setUpdatedAt(LocalDateTime.now());

        return imageRepository.save(image);
    }

    public void deleteImage(String imageId, String userId) {
        Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Image not found"));

        if (!image.getUserId().equals(userId)) {
            throw new RuntimeException("No permission to delete this image");
        }

        azureBlobService.deleteFile(image.getBlobUrl());
        imageRepository.delete(image);
    }

    public Image getImageById(String imageId) {
        return imageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Image not found"));
    }
}



