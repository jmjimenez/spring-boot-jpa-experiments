package es.jmjg.experiments.application.post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.jmjg.experiments.domain.entity.Post;
import es.jmjg.experiments.domain.repository.PostRepository;

@Service
public class FindAllPosts {

  private final PostRepository postRepository;

  public FindAllPosts(PostRepository postRepository) {
    this.postRepository = postRepository;
  }

  @Transactional(readOnly = true)
  public Page<Post> findAll(Pageable pageable) {
    return postRepository.findAll(pageable);
  }
}
