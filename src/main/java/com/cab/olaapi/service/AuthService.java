package com.cab.olaapi.service;


import com.cab.olaapi.config.jwtAuth.JwtTokenGenerator;
import com.cab.olaapi.dto.AuthResponseDto;
import com.cab.olaapi.dto.TokenType;
import com.cab.olaapi.dto.UserRegistrationDto;
import com.cab.olaapi.entity.RefreshToken;
import com.cab.olaapi.entity.User;
import com.cab.olaapi.mapper.UserMapper;
import com.cab.olaapi.repository.RefreshTokenRepository;
import com.cab.olaapi.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.net.http.HttpRequest;
import java.util.Arrays;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final UserRepository userRepository;
    private final JwtTokenGenerator jwtTokenGenerator;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserMapper userMapper;


    public AuthResponseDto getJwtTokenAfterAuthentication(Authentication authentication, HttpServletResponse response){
        try {
            var user = userRepository.findByEmailId(authentication.getName())
                    .orElseThrow(()->{
                        log.error("[AuthService:userSignInAuth] User : {}",authentication.getName());
                        return new ResponseStatusException(HttpStatus.NOT_FOUND,"USER NOT FOUND");
                    });
            
            String accessToken = jwtTokenGenerator.generateAccessToken(authentication);
            String refreshToken = jwtTokenGenerator.generateRefreshToken(authentication);
            
            saveUserRefreshToken(user,refreshToken);
            createRefreshTokenCookie(response,refreshToken);

            log.info("[AuthService:userSignInAuth] Access token for user:{}, has been generated",user.getUserName());

            return AuthResponseDto.builder()
                    .accessToken(accessToken)
                    .accessTokenExpiry(15*60)
                    .tokenType(TokenType.Bearer)
                    .build();
        }catch (Exception e){
            log.error("[AuthService:userSignInAuth]Exception while authenticating the user due to :"+e.getMessage());

            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"Please Try Again");
        }
    }


    private Cookie createRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        Cookie refreshTokenCookie = new Cookie("refresh_token",refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setMaxAge(15*24*60*60);
        response.addCookie(refreshTokenCookie);
        return refreshTokenCookie;
    }

    private void saveUserRefreshToken(User user, String refreshToken) {
        var refreshTokenEntry = RefreshToken.builder()
                .user(user)
                .refreshToken(refreshToken)
                .revoked(false)
                .build();
    }

    public Object getAccessTokenUsingRefreshToken(String authorizationHeader){
        if(!authorizationHeader.startsWith(TokenType.Bearer.name())){
            return new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"Please verify token type");
        }
        final String refreshToken = authorizationHeader.substring(7);

        var refreshTokenEntity = refreshTokenRepository.findByRefreshToken(refreshToken)
                .filter(tokens->!tokens.isRevoked())
                .orElseThrow(()->new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"Refresh token revoked"));

        User user = refreshTokenEntity.getUser();
        Authentication authentication = createAuthenticationObject(user);

        String accessToken = jwtTokenGenerator.generateAccessToken(authentication);

        return AuthResponseDto.builder()
                .accessToken(accessToken)
                .accessTokenExpiry(5*60)
                .userName(user.getUserName())
                .tokenType(TokenType.Bearer)
                .build();

    }

    private Authentication createAuthenticationObject(User user) {
        String username =user.getEmailId();
        String password = user.getPassword();
        String roles = user.getRoles();

        String[] roleArray = roles.split(",");
        GrantedAuthority[] authorities = Arrays.stream(roleArray)
                .map(role->(GrantedAuthority) role::trim)
                .toArray(GrantedAuthority[]::new);

        return new UsernamePasswordAuthenticationToken(username,password,Arrays.asList(authorities));
    }

    public AuthResponseDto registerUser(UserRegistrationDto userRegistrationDto,
                                        HttpServletResponse httpServletResponse){
        try{

            log.info("[AuthService: registerUser]User Registration started with::{}",userRegistrationDto);

            Optional<User> user = userRepository.findByEmailId(userRegistrationDto.userEmail());
            if(user.isPresent()){
                throw new Exception("User already exist");
            }

            User userDetailsEntity = userMapper.convertToEntity(userRegistrationDto);
            Authentication authentication = createAuthenticationObject(userDetailsEntity);

            String accessToken = jwtTokenGenerator.generateAccessToken(authentication);
            String refreshToken = jwtTokenGenerator.generateRefreshToken(authentication);

            User savedUserDetails = userRepository.save(userDetailsEntity);
            saveUserRefreshToken(userDetailsEntity,refreshToken);

            createRefreshTokenCookie(httpServletResponse,refreshToken);


            log.info("[AuthService: registerUser] User:{} successfully registered ",savedUserDetails.getUserName());

            return AuthResponseDto.builder()
                    .accessToken(accessToken)
                    .accessTokenExpiry(5*60)
                    .userName(savedUserDetails.getUserName())
                    .tokenType(TokenType.Bearer)
                    .build();


        }catch (Exception e){
            log.error("[AuthService:registerUser]Exception while registering the user due to: "+e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,e.getMessage());
        }
    }



}
