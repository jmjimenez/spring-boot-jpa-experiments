package es.jmjg.experiments.application.post;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.jmjg.experiments.domain.entity.Post;
import es.jmjg.experiments.domain.repository.PostRepository;
import es.jmjg.experiments.infrastructure.controller.exception.PostNotFoundException;

@Service
public class FindPostByUuid {
  
  private final PostRepository postRepository;

  public FindPostByUuid(PostRepository postRepository) {
    this.postRepository = postRepository;
  }

  @Transactional(readOnly = true)
  public Post findByUuid(UUID uuid) {
    if (uuid == null) {
      throw new IllegalArgumentException("UUID cannot be null");
    }

    return postRepository.findByUuid(uuid).orElseThrow(PostNotFoundException::new);
  }
}
