package com.manuonda.urlshortener.repositorys;

import com.manuonda.urlshortener.domain.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findById(Long id);
    boolean existsByEmail(String email);

     Optional<User> findByName(String username);

     Optional<User> findByEmail(String email);
}
