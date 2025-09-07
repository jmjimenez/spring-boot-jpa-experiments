package es.jmjg.experiments.application.post;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.jmjg.experiments.application.post.dto.SavePostDto;
import es.jmjg.experiments.domain.shared.exception.InvalidRequest;
import es.jmjg.experiments.domain.user.exception.UserNotFound;
import es.jmjg.experiments.domain.post.entity.Post;
import es.jmjg.experiments.domain.user.entity.User;
import es.jmjg.experiments.domain.post.repository.PostRepository;
import es.jmjg.experiments.domain.user.repository.UserRepository;

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
    Post post = new Post();
    post.setUuid(savePostDto.uuid());
    post.setTitle(savePostDto.title());
    post.setBody(savePostDto.body());

    if (savePostDto.authenticatedUser() == null) {
      throw new InvalidRequest("Post must have a user");
    }

    Optional<User> user = userRepository.findByUuid(savePostDto.authenticatedUser().id());
    if (user.isPresent()) {
      post.setUser(user.get());
    } else {
      throw new UserNotFound(savePostDto.authenticatedUser().id());
    }

    if (savePostDto.tagNames() != null) {
      processPostTags.processTagsForPost(post, savePostDto.tagNames());
    }

    return postRepository.save(post);
  }
}
