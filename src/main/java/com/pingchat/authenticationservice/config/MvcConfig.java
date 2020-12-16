package com.pingchat.authenticationservice.config;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
    // TODO: Move this to a configuration bean
    @Setter
    @Value("${service.static-base-path}")
    private String staticBasePath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String staticDirectoryPath = staticBasePath;

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
