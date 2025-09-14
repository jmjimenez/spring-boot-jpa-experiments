package es.jmjg.experiments.infrastructure.repository.jpa;

import es.jmjg.experiments.domain.post.entity.PostComment;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface JpaPostCommentRepository extends JpaRepository<PostComment, Integer> {

  @Transactional(readOnly = true)
  Optional<PostComment> findByUuid(UUID uuid);
}
