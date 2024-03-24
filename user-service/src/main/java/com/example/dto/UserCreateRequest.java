package com.example.dto;

import com.example.model.User;
import jakarta.validation.constraints.NotBlank;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCreateRequest {


    private String name;
    @NotBlank
    private String username;
    @NotBlank
    private String email;
    @NotBlank
    private String password;


    public User to(){
        return User.builder()
                .name(this.name)
                .username(this.username)
                .password(this.password)
                .email(this.email)
                .build();
    }
}
