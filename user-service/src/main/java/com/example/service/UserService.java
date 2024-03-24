package com.example.service;

import com.example.Utils.Constants;
import com.example.dto.UserCreateRequest;
import com.example.model.User;
import com.example.repository.UserCacheRepository;
import com.example.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserCacheRepository userCacheRepository;

    @Autowired
    KafkaTemplate<String,String> kafkaTemplate;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    PasswordEncoder passwordEncoder;



    public void createUser(UserCreateRequest userCreateRequest) throws JsonProcessingException {

        User user = userCreateRequest.to();
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setAuthorities("user");
        userRepository.save(user);

        JSONObject event = objectMapper.convertValue(user, JSONObject.class);
        String msg = objectMapper.writeValueAsString(event);

        kafkaTemplate.send(Constants.USER_CREATED_TOPIC,msg);
    }

    public User getDetails(int id) {

        User user = userCacheRepository.get(id);

        if(user==null){
            user = userRepository.findById(id).orElseThrow(()-> new NoSuchElementException());
            userCacheRepository.set(user);
        }

        return user;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return this.userRepository.findByUsername(username);
    }
}
