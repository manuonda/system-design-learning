package com.spring4.version;


import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class UserRepository {

    private final List<User> users = new ArrayList<>();

    public List<User> findAll() {
        return users;
    }

    public User findById(Integer id) {
        return users.stream().filter(u -> u.id().equals(id)).findFirst().orElse(null);
    }

    @PostConstruct
    public void init(){
        users.add(new User(1, "Dan Vega","danvega@gmail.com","https://www.danvega.com"));
    }

}
