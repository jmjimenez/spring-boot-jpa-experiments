package es.jmjg.experiments.infrastructure.controller;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import es.jmjg.experiments.application.PostService;
import es.jmjg.experiments.domain.Post;
import es.jmjg.experiments.infrastructure.controller.dto.PostRequestDto;
import es.jmjg.experiments.infrastructure.controller.dto.PostResponseDto;
import es.jmjg.experiments.infrastructure.controller.exception.PostNotFoundException;
import es.jmjg.experiments.infrastructure.controller.mapper.PostMapper;
import jakarta.validation.Valid;

// TODO: add exception handling
@RestController
@RequestMapping("/api/posts")
public class PostController {

    private static final Logger log = LoggerFactory.getLogger(PostController.class);
    private final PostService postService;
    private final PostMapper postMapper;

    public PostController(PostService postService, PostMapper postMapper) {
        this.postService = postService;
        this.postMapper = postMapper;
    }

    @GetMapping("")
    @Transactional(readOnly = true)
    List<PostResponseDto> findAll() {
        List<Post> posts = postService.findAll();
        return postMapper.toResponseDtoList(posts);
    }

    @GetMapping("/{id}")
    PostResponseDto findById(@PathVariable Integer id) {
        Post post = postService.findById(id).orElseThrow(PostNotFoundException::new);
        return postMapper.toResponseDto(post);
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    PostResponseDto save(@RequestBody @Valid PostRequestDto postDto) {
        // Convert DTO to domain entity
        Post post = postMapper.toDomain(postDto);
        // Set up the user relationship based on userId from DTO
        Post savedPost = postService.save(post, postDto.getUserId());
        return postMapper.toResponseDto(savedPost);
    }

    @PutMapping("/{id}")
    PostResponseDto update(@PathVariable Integer id, @RequestBody @Valid PostRequestDto postDto) {
        try {
            // Convert DTO to domain entity
            Post post = postMapper.toDomain(postDto);

            Post updatedPost = postService.update(id, post, postDto.getUserId());
            return postMapper.toResponseDto(updatedPost);
        } catch (RuntimeException e) {
            throw new PostNotFoundException();
        }
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    void delete(@PathVariable Integer id) {
        postService.deleteById(id);
    }
}
