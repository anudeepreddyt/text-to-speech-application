package com.anudeepreddy.text_to_speech_backend.Service;

import com.anudeepreddy.text_to_speech_backend.Model.UserPrincipal;
import com.anudeepreddy.text_to_speech_backend.Model.UsersModel;
import com.anudeepreddy.text_to_speech_backend.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UsersModel usersModel=userRepository.findByUsername(username).orElseThrow(()-> new RuntimeException("Usename not found"));
        return new UserPrincipal(usersModel);
    }
}
