package com.cab.olaapi.mapper;

import com.cab.olaapi.dto.UserRegistrationDto;
import com.cab.olaapi.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {
    private final PasswordEncoder passwordEncoder;

    public User convertToEntity(UserRegistrationDto userRegistrationDto){
        User user = new User();

        user.setUserName(userRegistrationDto.userName());
        user.setEmailId(userRegistrationDto.userEmail());
        user.setMobileNumber(userRegistrationDto.userMobileNo());
        user.setRoles(userRegistrationDto.userRole());
        user.setPassword(passwordEncoder.encode(userRegistrationDto.userPassword()));

        return user;
    }


}
