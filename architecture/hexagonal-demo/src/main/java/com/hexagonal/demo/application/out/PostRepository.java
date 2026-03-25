package com.hexagonal.demo.application.out;

import com.hexagonal.demo.domain.model.Post;

import java.util.List;

/**
 * Funcionalidades dentro del sistema
 */
public interface PostRepository {
    void save(Post post);
    void update(String id,Post post);
    List<Post> findAll();
    Post findById(String id);
}
