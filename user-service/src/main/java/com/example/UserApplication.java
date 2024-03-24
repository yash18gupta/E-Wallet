package com.example;

import com.example.model.User;
import com.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class UserApplication implements CommandLineRunner {
    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class, args);
    }

    @Autowired
    UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {


//        userRepository.save(
//                User.builder()
//                        .name("txnService")
//                        .email("txnservice@gmail.com")
//                        .username("txnService")
//                        .password(new BCryptPasswordEncoder().encode("txn123"))
//                        .authorities("service")
//                        .build()
//        );
//
//
//        userRepository.save(
//                User.builder()
//                        .name("notifService")
//                        .email("ewallet.jdbl.59@gmail.com")
//                        .username("notifService")
//                        .password(new BCryptPasswordEncoder().encode("notif123"))
//                        .authorities("service")
//                        .build()
//        );


    }
}