package com.imageshare.controller;

import com.imageshare.model.Image;
import com.imageshare.model.User;
import com.imageshare.repository.UserRepository;
import com.imageshare.service.AzureBlobService;
import com.imageshare.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/images")
@CrossOrigin(origins = "*")
public class ImageController {

    @Autowired
    private ImageService imageService;

    @Autowired
    private AzureBlobService azureBlobService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "name", required = false) String name,
            Authentication authentication) {
        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String userId = getUserIdByUsername(userDetails.getUsername());

            Image image = imageService.uploadImage(file, userId, name);

            Map<String, Object> response = new HashMap<>();
            response.put("id", image.getId());
            response.put("name", image.getName());
            response.put("message", "Upload successful");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping
    public ResponseEntity<List<Image>> getAllImages() {
        List<Image> images = imageService.getAllImages();
        return ResponseEntity.ok(images);
    }

    @GetMapping("/my")
    public ResponseEntity<List<Image>> getMyImages(Authentication authentication) {
        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String userId = getUserIdByUsername(userDetails.getUsername());

            List<Image> images = imageService.getUserImages(userId);
            return ResponseEntity.ok(images);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<InputStreamResource> downloadImage(@PathVariable String id) {
        try {
            Image image = imageService.getImageById(id);
            InputStream inputStream = azureBlobService.downloadFile(image.getBlobUrl());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(image.getContentType()));
            headers.setContentDispositionFormData("attachment", image.getName());

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(new InputStreamResource(inputStream));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateImageName(
            @PathVariable String id,
            @RequestBody Map<String, String> request,
            Authentication authentication) {
        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String userId = getUserIdByUsername(userDetails.getUsername());
            String newName = request.get("name");

            Image image = imageService.updateImageName(id, userId, newName);

            Map<String, Object> response = new HashMap<>();
            response.put("id", image.getId());
            response.put("name", image.getName());
            response.put("message", "Update successful");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteImage(@PathVariable String id, Authentication authentication) {
        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String userId = getUserIdByUsername(userDetails.getUsername());

            imageService.deleteImage(id, userId);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Delete successful");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    private String getUserIdByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getId();
    }
}

