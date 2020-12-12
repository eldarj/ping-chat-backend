package com.pingchat.authenticationservice.config;

import com.pingchat.authenticationservice.auth.ws.UserPresenceInterceptor;
import com.pingchat.authenticationservice.auth.ws.WebSocketHandshakeHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration()
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    private final WebSocketHandshakeHandler webSocketHandshakeHandler;

    private final UserPresenceInterceptor userPresenceInterceptor;

    public WebSocketConfig(WebSocketHandshakeHandler webSocketHandshakeHandler,
                           UserPresenceInterceptor userPresenceInterceptor) {
        this.webSocketHandshakeHandler = webSocketHandshakeHandler;
        this.userPresenceInterceptor = userPresenceInterceptor;
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.setApplicationDestinationPrefixes("/ws")
                .enableSimpleBroker("/rides", "/drivers");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws/connect")
                .setHandshakeHandler(webSocketHandshakeHandler)
                .setAllowedOrigins("*");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(userPresenceInterceptor);
    }
}
