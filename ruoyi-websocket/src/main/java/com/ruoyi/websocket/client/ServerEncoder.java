package com.ruoyi.websocket.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.utils.JsonUtils;

import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;
import java.util.Map;

public class ServerEncoder implements Encoder.Text<Map<String,Object>>  {
    @Override
    public String encode(Map<String,Object> map){
        try {
            ObjectMapper objectMapper = JsonUtils.getObjectMapper();
            return objectMapper.writeValueAsString(map);

        } catch ( JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void init(EndpointConfig endpointConfig) {

    }

    @Override
    public void destroy() {

    }
}
