package com.ruoyi.websocket.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * @author xycq
 * @version 1.0.0
 * @ClassName WebSocketConfig.java
 * @Description 即时通讯 开启WebSocket支持
 * @createTime 2022年06月16日
 */
@Configuration
public class WebSocketConfig {
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
}
