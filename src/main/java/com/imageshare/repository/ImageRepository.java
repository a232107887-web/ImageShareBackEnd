package com.imageshare.repository;

import com.imageshare.model.Image;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageRepository extends MongoRepository<Image, String> {
    List<Image> findByUserIdOrderByUploadDateDesc(String userId);
    List<Image> findAllByOrderByUploadDateDesc();
}




