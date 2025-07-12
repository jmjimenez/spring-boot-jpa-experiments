package es.jmjg.experiments.application.post;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.jmjg.experiments.infrastructure.repository.PostRepository;

@Service
public class DeletePostById {

  private final PostRepository postRepository;

  public DeletePostById(PostRepository postRepository) {
    this.postRepository = postRepository;
  }

  @Transactional
  public void deleteById(Integer id) {
    postRepository.deleteById(id);
  }
}
