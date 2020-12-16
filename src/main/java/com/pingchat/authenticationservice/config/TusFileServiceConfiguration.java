package com.pingchat.authenticationservice.config;

import lombok.Data;
import me.desair.tus.server.TusFileUploadService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class TusFileServiceConfiguration {
    @Value("${service.static-base-path}")
    protected String staticBasePath;

    @Bean
    public TusFileUploadService tusFileUploadService() {
        return new TusFileUploadService()
                .withStoragePath(staticBasePath)
                .withUploadURI("/api/data-space/upload")
                .withThreadLocalCache(true);
    }
}
