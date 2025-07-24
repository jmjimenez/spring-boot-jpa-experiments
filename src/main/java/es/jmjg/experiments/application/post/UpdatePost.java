package es.jmjg.experiments.application.post;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import es.jmjg.experiments.application.post.exception.PostNotFound;
import es.jmjg.experiments.application.user.exception.UserNotFound;
import es.jmjg.experiments.domain.entity.Post;
import es.jmjg.experiments.domain.entity.User;
import es.jmjg.experiments.domain.repository.PostRepository;
import es.jmjg.experiments.domain.repository.UserRepository;

@Service
public class UpdatePost {

  private final PostRepository postRepository;
  private final UserRepository userRepository;
  private final ProcessPostTags processPostTags;

  public UpdatePost(PostRepository postRepository, UserRepository userRepository,
      ProcessPostTags processPostTags) {
    this.postRepository = postRepository;
    this.userRepository = userRepository;
    this.processPostTags = processPostTags;
  }

  public Post update(Integer id, Post post) {
    return update(id, post, null, null);
  }

  public Post update(Integer id, Post post, UUID userUuid) {
    return update(id, post, userUuid, null);
  }

  @Transactional
  public Post update(Integer id, Post post, UUID userUuid, List<String> tagNames) {
    Optional<Post> existing = postRepository.findById(id);
    if (existing.isEmpty()) {
      throw new PostNotFound(id);
    }

    Post existingPost = existing.get();
    existingPost.setTitle(post.getTitle());
    existingPost.setBody(post.getBody());

    // Only update UUID if provided (to avoid overwriting existing UUID)
    if (post.getUuid() != null) {
      existingPost.setUuid(post.getUuid());
    }

    if (userUuid != null) {
      Optional<User> user = userRepository.findByUuid(userUuid);
      if (user.isPresent()) {
        existingPost.setUser(user.get());
      } else {
        throw new UserNotFound(userUuid);
      }
    } else if (post.getUser() != null) {
      existingPost.setUser(post.getUser());
    }

    // Process tags if provided
    if (tagNames != null) {
      processPostTags.processTagsForPost(existingPost, tagNames);
    }

    return postRepository.save(existingPost);
  }
}
