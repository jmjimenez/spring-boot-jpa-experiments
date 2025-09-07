package es.jmjg.experiments.application.post;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.jmjg.experiments.domain.post.entity.Post;
import es.jmjg.experiments.domain.post.repository.PostRepository;

@Service
public class FindPosts {

  private final PostRepository postRepository;

  public FindPosts(PostRepository postRepository) {
    this.postRepository = postRepository;
  }

  @Transactional(readOnly = true)
  public List<Post> find(String query, int limit) {
    if (query == null || query.trim().isEmpty()) {
      return List.of();
    }

    Pageable pageable = PageRequest.of(0, limit);

    return postRepository.searchByContent(query.trim(), pageable);
  }
}
