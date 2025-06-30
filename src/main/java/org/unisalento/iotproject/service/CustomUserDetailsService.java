package org.unisalento.iotproject.service;

import org.unisalento.iotproject.domain.User;
import org.unisalento.iotproject.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        final Optional<User> userOptional = userRepository.findByEmail(email);

        if(userOptional.isEmpty()) {
            throw new UsernameNotFoundException(email);
        }

        User user = userOptional.get();

        UserDetails userDetails = org.springframework.security.core.userdetails.User.withUsername(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRole())
                .build();

        return userDetails;
    }
}
