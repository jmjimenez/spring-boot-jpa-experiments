package es.jmjg.experiments.application.post;

import es.jmjg.experiments.domain.post.entity.PostUuid;

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
  public Post findByUuid(PostUuid postUuid) {
    if (postUuid == null) {
      throw new IllegalArgumentException("UUID cannot be null");
    }

    return postRepository.findByPostUuid(postUuid).orElseThrow(() -> new PostNotFound("Post with UUID " + postUuid + " not found"));
  }
}
