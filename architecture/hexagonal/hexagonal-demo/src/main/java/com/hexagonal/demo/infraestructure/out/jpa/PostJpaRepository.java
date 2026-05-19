package com.hexagonal.demo.infraestructure.out.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PostJpaRepository extends JpaRepository<PostJpaEntity, String> {

}
