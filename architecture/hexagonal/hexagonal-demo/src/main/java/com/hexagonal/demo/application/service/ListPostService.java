package com.hexagonal.demo.application.service;

import com.hexagonal.demo.application.in.ListPostUseCase;
import com.hexagonal.demo.application.out.PostRepository;
import com.hexagonal.demo.domain.model.Post;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * Implemente la parte de List Post Service
 */
@Service
public class ListPostService implements ListPostUseCase {

    private final PostRepository postRepository;

    public ListPostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }


    @Override
    public List<Post> list() {
        return this.postRepository.findAll()
                .stream()
                .toList();
    }
}
