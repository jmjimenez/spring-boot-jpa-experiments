package es.jmjg.experiments.application.post;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.jmjg.experiments.application.post.dto.SavePostDto;
import es.jmjg.experiments.application.shared.exception.InvalidRequest;
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

  @Transactional
  public Post save(SavePostDto savePostDto) {
    // Create a new Post object from the DTO
    Post post = new Post();
    post.setUuid(savePostDto.uuid());
    post.setTitle(savePostDto.title());
    post.setBody(savePostDto.body());

    // Validate that a user is required for creating a post
    if (savePostDto.userUuid() == null) {
      throw new InvalidRequest("Post must have a user");
    }

    // Set up the user relationship
    Optional<User> user = userRepository.findByUuid(savePostDto.userUuid());
    if (user.isPresent()) {
      post.setUser(user.get());
    } else {
      throw new UserNotFound(savePostDto.userUuid());
    }

    // Process tags if provided
    if (!savePostDto.tagNames().isEmpty()) {
      processPostTags.processTagsForPost(post, savePostDto.tagNames());
    }

    return postRepository.save(post);
  }
}
