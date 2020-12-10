package com.pingchat.authenticationservice.config;

import com.pingchat.authenticationservice.service.files.StaticFileStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Slf4j
@EnableWebMvc
@Configuration
public class MvcConfig implements WebMvcConfigurer {
    private final StaticFileStorageService staticFileStorageService;

    public MvcConfig(StaticFileStorageService staticFileStorageService) {
        this.staticFileStorageService = staticFileStorageService;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String staticDirectoryPath = staticFileStorageService.getStaticDirectoryPath();

        try {
            initializeStaticDirectory(staticDirectoryPath);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        registry
                .addResourceHandler("/files/**")
                .addResourceLocations("file:" + staticDirectoryPath + "/")
                .setCachePeriod(3600);
    }

    private void initializeStaticDirectory(String staticDirectoryPath) throws IOException {
        Files.createDirectories(Paths.get(staticDirectoryPath));
    }
}
