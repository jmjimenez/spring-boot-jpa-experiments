package es.jmjg.experiments.application.post;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import es.jmjg.experiments.application.post.exception.InvalidRequest;
import es.jmjg.experiments.application.user.exception.UserNotFound;
import es.jmjg.experiments.domain.entity.Post;
import es.jmjg.experiments.domain.entity.User;
import es.jmjg.experiments.domain.repository.PostRepository;
import es.jmjg.experiments.domain.repository.UserRepository;

@Service
public class SavePost {

  private final PostRepository postRepository;
  private final UserRepository userRepository;
  private final ProcessPostTags processPostTags;

  public SavePost(PostRepository postRepository, UserRepository userRepository,
      ProcessPostTags processPostTags) {
    this.postRepository = postRepository;
    this.userRepository = userRepository;
    this.processPostTags = processPostTags;
  }

  public Post save(Post post) {
    return save(post, null, null);
  }

  public Post save(Post post, UUID userUuid) {
    return save(post, userUuid, null);
  }

  @Transactional
  public Post save(Post post, UUID userUuid, List<String> tagNames) {
    // Validate that a user is required for creating a post
    if (userUuid == null && post.getUser() == null) {
      throw new InvalidRequest("Post must have a user");
    }

    // If the post has a userUuid but no user relationship, set up the relationship
    if (userUuid != null) {
      Optional<User> user = userRepository.findByUuid(userUuid);
      if (user.isPresent()) {
        post.setUser(user.get());
      } else {
        throw new UserNotFound(userUuid);
      }
    }

    // Process tags if provided
    if (tagNames != null) {
      processPostTags.processTagsForPost(post, tagNames);
    }

    return postRepository.save(post);
  }
}
