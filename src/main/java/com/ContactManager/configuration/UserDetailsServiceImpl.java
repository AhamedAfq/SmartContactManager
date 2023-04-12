package com.ContactManager.configuration;

import com.ContactManager.dao.UserRepository;
import com.ContactManager.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    // Fetching user from database using email , which is the userName in my case

        User user = userRepository.getUserByUserName(username);

        if(user == null){
            throw new UsernameNotFoundException("Could not find user !!!");
        }
        CustomUserDetails customUserDetails = new CustomUserDetails(user);
        return customUserDetails;
    }
}
