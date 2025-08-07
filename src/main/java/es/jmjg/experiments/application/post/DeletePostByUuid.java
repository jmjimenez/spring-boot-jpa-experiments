package es.jmjg.experiments.application.post;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.jmjg.experiments.application.post.exception.PostNotFound;
import es.jmjg.experiments.domain.repository.PostRepository;

@Service
public class DeletePostByUuid {

  private final PostRepository postRepository;

  public DeletePostByUuid(PostRepository postRepository) {
    this.postRepository = postRepository;
  }

  @Transactional
  public void deleteById(Integer id) {
    postRepository.deleteById(id);
  }

  @Transactional
  public void deleteByUuid(UUID uuid) {
    var post = postRepository.findByUuid(uuid);
    if (post.isEmpty()) {
      throw new PostNotFound(uuid);
    }
    postRepository.deleteById(post.get().getId());
  }
}
