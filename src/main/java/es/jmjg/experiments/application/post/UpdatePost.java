package es.jmjg.experiments.application.post;

import java.util.Optional;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.jmjg.experiments.application.post.exception.Forbidden;
import es.jmjg.experiments.application.post.exception.PostNotFound;
import es.jmjg.experiments.domain.entity.Post;
import es.jmjg.experiments.domain.repository.PostRepository;
import es.jmjg.experiments.domain.repository.UserRepository;
import es.jmjg.experiments.infrastructure.config.security.JwtUserDetailsService;

@Service
public class UpdatePost {

  private final PostRepository postRepository;
  private final ProcessPostTags processPostTags;

  public UpdatePost(PostRepository postRepository, UserRepository userRepository,
      ProcessPostTags processPostTags) {
    this.postRepository = postRepository;
    this.processPostTags = processPostTags;
  }

  @Transactional
  public Post update(UpdatePostDto updatePostDto) {
    Optional<Post> existing = postRepository.findByUuid(updatePostDto.uuid());
    if (existing.isEmpty()) {
      throw new PostNotFound(updatePostDto.uuid());
    }

    Post existingPost = existing.get();

    // Check if the user is the owner of the post
    if (!existingPost.getUser().getUuid().equals(updatePostDto.userDetails().id) &&
        !updatePostDto.userDetails().getAuthorities()
            .contains(new SimpleGrantedAuthority(JwtUserDetailsService.ROLE_ADMIN))) {
      throw new Forbidden("You are not the owner of this post");
    }

    existingPost.setTitle(updatePostDto.title());
    existingPost.setBody(updatePostDto.body());

    // Process tags if provided
    if (updatePostDto.tagNames() != null) {
      processPostTags.processTagsForPost(existingPost, updatePostDto.tagNames());
    }

    return postRepository.save(existingPost);
  }
}
