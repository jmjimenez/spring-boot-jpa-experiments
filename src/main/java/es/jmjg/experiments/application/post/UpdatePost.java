package es.jmjg.experiments.application.post;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.jmjg.experiments.application.post.exception.PostNotFound;
import es.jmjg.experiments.domain.entity.Post;
import es.jmjg.experiments.domain.repository.PostRepository;
import es.jmjg.experiments.domain.repository.UserRepository;

@Service
public class UpdatePost {

  private final PostRepository postRepository;
  private final ProcessPostTags processPostTags;

  public UpdatePost(PostRepository postRepository, UserRepository userRepository,
      ProcessPostTags processPostTags) {
    this.postRepository = postRepository;
    this.processPostTags = processPostTags;
  }

  public Post update(UUID uuid, Post post) {
    return update(uuid, post, null);
  }

  @Transactional
  public Post update(UUID uuid, Post post, List<String> tagNames) {
    return update(uuid, post, tagNames, null);
  }

  @Transactional
  public Post update(UUID uuid, Post post, List<String> tagNames, UUID userUuid) {
    Optional<Post> existing = postRepository.findByUuid(uuid);
    if (existing.isEmpty()) {
      throw new PostNotFound(uuid);
    }

    Post existingPost = existing.get();
    existingPost.setTitle(post.getTitle());
    existingPost.setBody(post.getBody());

    // Process tags if provided
    if (tagNames != null) {
      processPostTags.processTagsForPost(existingPost, tagNames);
    }

    return postRepository.save(existingPost);
  }
}
