package com.example.Config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.parser.JSONParser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class MapperConfig {

    @Bean
    public JSONParser getPaser(){
        return new JSONParser();
    }


    @Bean
    public ObjectMapper getMapper(){
        return new ObjectMapper();
    }


    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
