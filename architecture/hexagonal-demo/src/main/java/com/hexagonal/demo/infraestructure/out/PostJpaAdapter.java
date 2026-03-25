package com.hexagonal.demo.infraestructure.out;

import com.hexagonal.demo.infraestructure.out.jpa.PostJpaEntity;
import com.hexagonal.demo.infraestructure.out.jpa.PostJpaMapper;
import com.hexagonal.demo.infraestructure.out.jpa.PostJpaRepository;
import com.hexagonal.demo.application.out.PostRepository;
import com.hexagonal.demo.domain.model.Post;
import org.hibernate.ObjectNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;


/**
 * Adaptador de salida (Driven Adapter) para la persistencia de Post.
 * Implementa el puerto {@link PostRepository} definido en la capa de aplicación,
 * desacoplando la lógica de negocio de los detalles de infraestructura de JPA.
 * Este adaptador se encarga de traducir las operaciones del dominio a operaciones
 * de base de datos utilizando Spring Data JPA y mapear entre el modelo de dominio {@link com.hexagonal.demo.domain.model.Post}
 * y la entidad JPA {@link PostJpaEntity}.
 */
@Component
public class PostJpaAdapter implements PostRepository {

    private final PostJpaRepository postJpaRepository;
    private final PostJpaMapper postJpaMapper;

    public PostJpaAdapter(PostJpaRepository postJpaRepository,
                          PostJpaMapper postJpaMapper) {
        this.postJpaRepository = postJpaRepository;
        this.postJpaMapper = postJpaMapper;
    }
    @Override
    public void save(Post post) {
        PostJpaEntity postJpaEntity = new PostJpaEntity();
        postJpaEntity = this.postJpaMapper.mapToJpaEntity(post);
        this.postJpaRepository.save(postJpaEntity);
    }

    @Override
    public void update(String id, Post post) {
        PostJpaEntity postJpaEntity = new PostJpaEntity();
        Optional<PostJpaEntity> postJpaEntityOptional = this.postJpaRepository.findById(id);
        if (!postJpaEntityOptional.isEmpty()) {
            throw new IllegalArgumentException("Post not found");
        }
        postJpaEntity = postJpaEntityOptional.get();
        postJpaEntity.setTitle(post.getTitle());
        postJpaEntity.setContent(post.getContent());
        this.postJpaRepository.save(postJpaEntity);
    }

    @Override
    public List<Post> findAll() {
        return this.postJpaMapper.toDomainList(this.postJpaRepository.findAll());

    }

    @Override
    public Post findById(String id) {
        Optional<PostJpaEntity> postJpaEntityOptional = this.postJpaRepository.findById(id);
        if (!postJpaEntityOptional.isPresent()) {
            throw new IllegalArgumentException("Post not found");
        }
        PostJpaEntity postJpaEntity = postJpaEntityOptional.get();
        return postJpaMapper.mapToDomain(postJpaEntity);
    }

}
