package es.jmjg.experiments.application.post;

import es.jmjg.experiments.application.post.dto.FindAllPostsDto;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.jmjg.experiments.domain.post.entity.Post;
import es.jmjg.experiments.domain.post.repository.PostRepository;

@Service
public class FindAllPosts {

  private final PostRepository postRepository;

  public FindAllPosts(PostRepository postRepository) {
    this.postRepository = postRepository;
  }

  @Transactional(readOnly = true)
  public Page<Post> findAll(FindAllPostsDto dto) {
    return postRepository.findAll(dto.pageable());
  }
}
