package es.jmjg.experiments.domain.post.repository;

import es.jmjg.experiments.domain.post.entity.PostComment;
import java.util.Optional;
import java.util.UUID;

public interface PostCommentRepository {
  PostComment save(PostComment postComment);
  Optional<PostComment> findByUuid(UUID uuid);
}
