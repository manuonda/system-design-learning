package com.hexagonal.demo.infraestructure.in;


import com.hexagonal.demo.application.in.ListPostUseCase;
import com.hexagonal.demo.infraestructure.in.web.CreatePostRequest;
import com.hexagonal.demo.application.in.CreatePostUseCase;
import com.hexagonal.demo.infraestructure.in.web.PostResponse;
import com.hexagonal.demo.infraestructure.in.web.PostWebMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {


    private final CreatePostUseCase  createPostUseCase;
    private final ListPostUseCase listPostUseCase;
    private final PostWebMapper postWebMapper;

    public PostController(CreatePostUseCase createPostUseCase,
                          ListPostUseCase listPostUseCase,
                          PostWebMapper postWebMapper) {

        this.createPostUseCase = createPostUseCase;
        this.listPostUseCase = listPostUseCase;
        this.postWebMapper = postWebMapper;
    }

    @PostMapping("/create")
    public ResponseEntity<String> create(@RequestBody CreatePostRequest createPostRequest ) {
      this.createPostUseCase.create(createPostRequest.title(), createPostRequest.content());
      return ResponseEntity.status(HttpStatus.OK).body("Post created");
    }

    @GetMapping
    public List<PostResponse> findAll() {
        return this.postWebMapper.toListPostResponse(this.listPostUseCase.list());
    }
}
