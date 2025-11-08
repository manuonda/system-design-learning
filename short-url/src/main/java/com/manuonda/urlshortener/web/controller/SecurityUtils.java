package com.manuonda.urlshortener.web.controller;


import com.manuonda.urlshortener.domain.entities.User;
import com.manuonda.urlshortener.repositorys.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class SecurityUtils {

    private final UserRepository userRepository;
    public SecurityUtils(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
   public User getCurrentUser(){
       Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
       if(authentication != null && authentication.isAuthenticated()){
              return this.userRepository.findByEmail(authentication.getName()).orElse(null);
       }
       return null;
   }

    public Long getCurrentUserId() {
        User user = getCurrentUser();
        return user != null ? user.getId() : null;
    }
}
