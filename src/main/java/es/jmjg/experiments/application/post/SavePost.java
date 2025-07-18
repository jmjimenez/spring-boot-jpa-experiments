package es.jmjg.experiments.application.post;

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

  public SavePost(PostRepository postRepository, UserRepository userRepository) {
    this.postRepository = postRepository;
    this.userRepository = userRepository;
  }

  public Post save(Post post) {
    return save(post, null);
  }

  @Transactional
  public Post save(Post post, UUID userUuid) {
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
    return postRepository.save(post);
  }
}
