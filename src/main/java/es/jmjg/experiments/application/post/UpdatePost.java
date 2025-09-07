package es.jmjg.experiments.application.post;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.jmjg.experiments.application.post.dto.UpdatePostDto;
import es.jmjg.experiments.domain.post.exception.PostNotFound;
import es.jmjg.experiments.domain.shared.exception.Forbidden;
import es.jmjg.experiments.domain.post.entity.Post;
import es.jmjg.experiments.domain.post.repository.PostRepository;

@Service
public class UpdatePost {

  private final PostRepository postRepository;
  private final ProcessPostTags processPostTags;

  public UpdatePost(PostRepository postRepository, ProcessPostTags processPostTags) {
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

    if (!existingPost.getUser().getUuid().equals(updatePostDto.authenticatedUser().id()) &&
        !updatePostDto.authenticatedUser().isAdmin()) {
      throw new Forbidden("You are not the owner of this post");
    }

    existingPost.setTitle(updatePostDto.title());
    existingPost.setBody(updatePostDto.body());

    if (updatePostDto.tagNames() != null) {
      processPostTags.processTagsForPost(existingPost, updatePostDto.tagNames());
    }

    return postRepository.save(existingPost);
  }
}
