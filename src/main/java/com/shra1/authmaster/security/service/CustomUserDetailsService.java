package com.shra1.authmaster.security.service;

import com.shra1.authmaster.dbmodels.User;
import com.shra1.authmaster.dbmodels.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class CustomUserDetailsService implements UserDetailsService {

   @Autowired
   UserRepository userRepository;

   @Override
   public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
      User user = userRepository.findByUsername(username);
      if (user == null) {
         throw new UsernameNotFoundException("No user found with username: " + username);
      }
      return org.springframework.security.core
        .userdetails
        .User
        .builder()
        .username(user.getUsername())
        .password(user.getPassword())
        .roles(user.getRole())
        .build();
   }
}
