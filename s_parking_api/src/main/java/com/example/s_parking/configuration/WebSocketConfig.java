package com.example.s_parking.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Điểm client kết nối tới
        registry.addEndpoint("/ws-parking")
                .setAllowedOriginPatterns("*") // Cho phép mọi client (có thể fix domain)
                .withSockJS(); // Cho phép client fallback nếu không hỗ trợ websocket thuần
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Các topic để gửi dữ liệu về client
        registry.enableSimpleBroker("/topic");

        // Prefix cho các endpoint client gửi về server
        registry.setApplicationDestinationPrefixes("/app");
    }
}
