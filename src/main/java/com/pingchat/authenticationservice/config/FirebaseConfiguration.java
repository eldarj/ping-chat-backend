package com.pingchat.authenticationservice.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;

@Configuration
public class FirebaseConfiguration {
    @Bean
    FirebaseMessaging firebaseMessaging() throws IOException {
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        Resource resource = resourceLoader.getResource("classpath:firebase-service-account.json");
        GoogleCredentials googleCredentials = GoogleCredentials.fromStream(resource.getInputStream());

        FirebaseOptions firebaseOptions = FirebaseOptions.builder()
                .setCredentials(googleCredentials)
                .build();

        FirebaseApp app = FirebaseApp.initializeApp(firebaseOptions, "ping-chat");

        return FirebaseMessaging.getInstance(app);
    }
}
