package com.sanviitech.mortextBank.mapper;

import com.sanviitech.mortextBank.dto.UserResponse;
import com.sanviitech.mortextBank.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    
    public UserResponse toResponse(User user) {
        if (user == null) {
            return null;
        }
        
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setFullName(user.getFullName());
        response.setEmail(user.getEmail());
        return response;
    }
    
    public User toEntity(com.sanviitech.mortextBank.dto.RegisterRequest request) {
        if (request == null) {
            return null;
        }
        
        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setSecurityPin(request.getSecurityPin());
        return user;
    }
}
