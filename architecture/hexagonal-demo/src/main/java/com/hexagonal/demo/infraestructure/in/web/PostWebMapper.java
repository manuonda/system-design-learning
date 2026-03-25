package com.hexagonal.demo.infraestructure.in.web;


import com.hexagonal.demo.domain.model.Post;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PostWebMapper {

    public PostResponse toPostResponse(Post post) {
        PostResponse postResponse = new PostResponse(post.getId(),post.getTitle(),post.getContent());
        return postResponse;
    }

    public List<PostResponse> toListPostResponse(List<Post> posts){
         return posts
                 .stream().map(this::toPostResponse)
                 .toList();
    }

}
