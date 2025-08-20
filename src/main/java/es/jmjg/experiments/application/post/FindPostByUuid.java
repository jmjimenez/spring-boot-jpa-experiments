package es.jmjg.experiments.application.post;

import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.jmjg.experiments.domain.entity.Post;
import es.jmjg.experiments.domain.repository.PostRepository;

@Service
public class FindPostByUuid {

  private static final Logger logger = LoggerFactory.getLogger(FindPostByUuid.class);

  private final PostRepository postRepository;

  public FindPostByUuid(PostRepository postRepository) {
    this.postRepository = postRepository;
  }

  @Transactional(readOnly = true)
  public Optional<Post> findByUuid(UUID uuid) {
    logger.debug("FindPostByUuid.findByUuid() called with UUID: {} - Transaction started", uuid);

    if (uuid == null) {
      logger.debug("UUID is null, returning empty Optional");
      return Optional.empty();
    }

    try {
      Optional<Post> result = postRepository.findByUuid(uuid);
      if (result.isPresent()) {
        Post post = result.get();
        logger.debug("Post found successfully. Post ID: {}, Title: {}, User ID: {}",
            post.getId(), post.getTitle(), post.getUser() != null ? post.getUser().getId() : "null");
      } else {
        logger.debug("No post found for UUID: {}", uuid);
      }
      logger.debug("FindPostByUuid.findByUuid() completed - Transaction will end after this method");
      return result;
    } catch (Exception e) {
      logger.error("Error occurred in FindPostByUuid.findByUuid() for UUID: {}. Error: {}", uuid, e.getMessage(), e);
      throw e;
    }
  }
}
