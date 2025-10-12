package es.jmjg.experiments.domain.post.repository;

import es.jmjg.experiments.domain.post.entity.PostComment;
import es.jmjg.experiments.domain.post.entity.PostCommentUuid;
import java.util.Optional;

public interface PostCommentRepository {
  PostComment save(PostComment postComment);
  Optional<PostComment> findByCommentUuid(PostCommentUuid uuid);
  void deleteById(Integer id);
}
