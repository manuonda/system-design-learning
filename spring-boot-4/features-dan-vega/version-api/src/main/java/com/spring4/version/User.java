package com.spring4.version;

public record User(

        Integer id,
        String name,
        String email,
        String webSite
) {

     public User{
         if(email.isBlank()) {
             throw new IllegalArgumentException("Email cannot be blank");
         }
     }
}
