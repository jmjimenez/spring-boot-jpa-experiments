package es.jmjg.experiments.application.post;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.jmjg.experiments.application.post.exception.InvalidRequest;
import es.jmjg.experiments.application.user.exception.UserNotFound;
import es.jmjg.experiments.domain.Post;
import es.jmjg.experiments.domain.User;
import es.jmjg.experiments.infrastructure.repository.PostRepository;
import es.jmjg.experiments.infrastructure.repository.UserRepository;

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
  public Post save(Post post, Integer userId) {
    // Validate that a user is required for creating a post
    if (userId == null && post.getUser() == null) {
      throw new InvalidRequest("Post must have a user");
    }

    // If the post has a userId but no user relationship, set up the relationship
    if (userId != null) {
      Optional<User> user = userRepository.findById(userId);
      if (user.isPresent()) {
        post.setUser(user.get());
      } else {
        throw new UserNotFound(userId);
      }
    }
    return postRepository.save(post);
  }
}
