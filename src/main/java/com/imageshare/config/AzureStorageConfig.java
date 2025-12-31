package com.imageshare.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "azure.storage")
public class AzureStorageConfig {
    private String connectionString;
    private String containerName;
}



