package com.hexagonal.demo.infraestructure.out.jpa;


import com.hexagonal.demo.domain.model.Post;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PostJpaMapper {

    public PostJpaEntity mapToJpaEntity(Post post) {
        PostJpaEntity jpaEntity = new PostJpaEntity();
        jpaEntity.setId(post.getId());
        jpaEntity.setTitle(post.getTitle());
        jpaEntity.setContent(post.getContent());
        return jpaEntity;
    }

    public Post mapToDomain(PostJpaEntity jpaEntity) {
        Post post = new Post();
        post.setId(jpaEntity.getId());
        post.setTitle(jpaEntity.getTitle());
        post.setContent(jpaEntity.getContent());
        return post;
    }

    public List<Post> toDomainList(List<PostJpaEntity> postJpaEntities) {
        return postJpaEntities.stream()
                .map(this::mapToDomain)
                .toList();
    }
}
