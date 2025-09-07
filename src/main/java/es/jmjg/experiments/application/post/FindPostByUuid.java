package es.jmjg.experiments.application.post;

import java.util.UUID;

import es.jmjg.experiments.domain.post.exception.PostNotFound;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.jmjg.experiments.domain.post.entity.Post;
import es.jmjg.experiments.domain.post.repository.PostRepository;

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

    return postRepository.findByUuid(uuid).orElseThrow(() -> new PostNotFound("Post with UUID " + uuid + " not found"));
  }
}
