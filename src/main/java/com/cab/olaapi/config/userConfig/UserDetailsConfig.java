package com.cab.olaapi.config.userConfig;

import com.cab.olaapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsConfig implements UserDetailsService {
    private final UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String emailId) throws UsernameNotFoundException {
        return userRepository
                .findByEmailId(emailId)
                .map(UserConfig::new)
                .orElseThrow(()->new UsernameNotFoundException("User is not found by "+emailId));
    }
}
