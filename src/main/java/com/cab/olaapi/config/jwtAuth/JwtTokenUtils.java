package com.cab.olaapi.config.jwtAuth;

import com.cab.olaapi.config.userConfig.UserConfig;
import com.cab.olaapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Objects;

@RequiredArgsConstructor
@Component
public class JwtTokenUtils {

    public String getUserName(Jwt jwtToken){
        return jwtToken.getSubject();
    }

    public boolean isTokenValid(Jwt jwtToken, UserDetails userDetails){
        final String userName = getUserName(jwtToken);
        boolean isTokenExpired = getTokenExpired(jwtToken);
        boolean isTokenUserSameAsDatabse = userName.equals(userDetails.getUsername());
        return !isTokenExpired && isTokenUserSameAsDatabse;
    }

    private boolean getTokenExpired(Jwt jwtToken) {
        return Objects.requireNonNull(jwtToken.getExpiresAt()).isBefore(Instant.now());
    }

    private final UserRepository userRepository;
    public UserDetails userDetails(String emailId){
        return userRepository
                .findByEmailId(emailId)
                .map(UserConfig::new)
                .orElseThrow(()->new UsernameNotFoundException("UserEmail: "+emailId+" does not exist"));
    }

}
