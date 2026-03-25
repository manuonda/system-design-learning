package com.hexagonal.demo.application.service;

import com.hexagonal.demo.application.in.CreatePostUseCase;
import com.hexagonal.demo.application.out.PostRepository;
import com.hexagonal.demo.domain.model.Post;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Implementa el puerto de entrada y usa el puerto de salida
 * Es la conexion de los puertos de entrada y salida
 */
@Service
public class CreatePostService implements CreatePostUseCase {

    private final PostRepository postRepository;

    public CreatePostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }


    @Override
    public void create(String title, String content) {
        Post post = new Post(UUID.randomUUID().toString(), title, content);
        this.postRepository.save(post);
    }
}
