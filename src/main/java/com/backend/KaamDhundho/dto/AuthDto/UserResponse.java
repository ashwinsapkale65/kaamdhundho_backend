package com.backend.KaamDhundho.dto.AuthDto;


import com.backend.KaamDhundho.entity.auth.User;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserResponse {
    private User user; // The user data

    public UserResponse(User user) {
        this.user = user;
    }

}

