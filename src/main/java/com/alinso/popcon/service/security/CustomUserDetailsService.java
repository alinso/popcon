package com.alinso.popcon.service.security;

import com.alinso.popcon.entity.User;
import com.alinso.popcon.exception.UserWarningException;
import com.alinso.popcon.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

            User user = userRepository.findByUsername(username);
            if(user==null)
                throw new UserWarningException("Kullanıcı adı veya şifre yanlış");

            return user;
    }


    public User loadUserById(long id) {
        User user = userRepository.findById(id).get();
        return user;
    }
}
