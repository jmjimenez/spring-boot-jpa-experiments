package es.jmjg.experiments.application.post;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.jmjg.experiments.domain.entity.Post;
import es.jmjg.experiments.domain.repository.PostRepository;

@Service
public class FindPostByUuid {
  
  private final PostRepository postRepository;

  public FindPostByUuid(PostRepository postRepository) {
    this.postRepository = postRepository;
  }

  @Transactional(readOnly = true)
  public Optional<Post> findByUuid(UUID uuid) {
    if (uuid == null) {
      return Optional.empty();
    }

    return postRepository.findByUuid(uuid);
  }
}
