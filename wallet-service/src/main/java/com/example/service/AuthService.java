package com.example.service;

import com.example.model.User;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;

@Service
public class AuthService implements UserDetailsService {

    @Autowired
    RestTemplate restTemplate;

    private static Logger logger = LoggerFactory.getLogger(AuthService.class);


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String url = "http://localhost:8000/user/username/" + username;

        // Creating authorization header for txn service
        String plainCreds = "walletService:wallet123";
        byte[] plainCredsBytes = plainCreds.getBytes();
        byte[] base64CredsBytes = Base64.getEncoder().encode(plainCredsBytes);
        String base64Creds = new String(base64CredsBytes);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + base64Creds);
        HttpEntity<String> request = new HttpEntity<>(headers);

        JSONObject userData = restTemplate.exchange(url, HttpMethod.GET, request, JSONObject.class).getBody();

        logger.info("user data - {}", userData);


        return User.builder()
                .username((String)userData.get("username"))
                .password((String) userData.get("password"))
                .email((String) userData.get("email"))
                .name((String) userData.get("name"))
                .build();

    }
}
