package com.example.hexagonal.infraestructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;


public interface SpringDataUserRepository extends JpaRepository<UserEntity,Long>{

}
