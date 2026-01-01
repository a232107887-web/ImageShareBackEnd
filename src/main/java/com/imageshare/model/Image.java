package com.imageshare.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "images")
public class Image {
    @Id
    private String id;
    
    private String name;
    
    private String userId;
    
    private String authorName;
    
    private String blobUrl;
    
    private String contentType;
    
    private Long fileSize;

}




