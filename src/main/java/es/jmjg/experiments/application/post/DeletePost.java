package es.jmjg.experiments.application.post;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.jmjg.experiments.application.post.dto.DeletePostDto;
import es.jmjg.experiments.application.post.exception.PostNotFound;
import es.jmjg.experiments.application.shared.exception.Forbidden;
import es.jmjg.experiments.domain.repository.PostRepository;
import es.jmjg.experiments.infrastructure.config.security.JwtUserDetailsService;

@Service
public class DeletePost {

  private final PostRepository postRepository;

  public DeletePost(PostRepository postRepository) {
    this.postRepository = postRepository;
  }

  @Transactional
  public void delete(DeletePostDto deletePostDto) {
    var post = postRepository.findByUuid(deletePostDto.uuid());

    if (post.isEmpty()) {
      throw new PostNotFound(deletePostDto.uuid());
    }

    if (!post.get().getUser().getUuid().equals(deletePostDto.userDetails().id) &&
        !deletePostDto.userDetails().getAuthorities()
            .contains(new SimpleGrantedAuthority(JwtUserDetailsService.ROLE_ADMIN))) {
      throw new Forbidden("You are not the owner of this post");
    }
    postRepository.deleteById(post.get().getId());
  }
}
