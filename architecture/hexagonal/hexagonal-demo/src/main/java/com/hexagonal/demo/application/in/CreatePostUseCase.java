package com.hexagonal.demo.application.in;

import com.hexagonal.demo.domain.model.Post;

/**
 * Port Use Case Create Post
 * Puerto de Entrada
 */
public interface CreatePostUseCase {
   void create(String title, String content);
}
