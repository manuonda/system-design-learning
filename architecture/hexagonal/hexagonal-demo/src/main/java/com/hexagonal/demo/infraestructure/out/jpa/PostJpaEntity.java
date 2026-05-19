package com.hexagonal.demo.infraestructure.out.jpa;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="posts")
@Getter
@Setter
public class PostJpaEntity {

    @Id
    private String id;
    private String title;
    private String content;
}
