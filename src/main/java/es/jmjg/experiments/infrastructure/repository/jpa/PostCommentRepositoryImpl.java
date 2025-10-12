package es.jmjg.experiments.infrastructure.repository.jpa;

import es.jmjg.experiments.domain.post.entity.PostComment;
import es.jmjg.experiments.domain.post.entity.PostCommentUuid;
import es.jmjg.experiments.domain.post.repository.PostCommentRepository;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public class PostCommentRepositoryImpl implements PostCommentRepository {

  private final JpaPostCommentRepository jpaPostCommentRepository;

  public PostCommentRepositoryImpl(JpaPostCommentRepository jpaPostCommentRepository) {
    this.jpaPostCommentRepository = jpaPostCommentRepository;
  }

  @Override
  @Transactional
  public PostComment save(PostComment postComment) {
    return jpaPostCommentRepository.save(postComment);
  }

  @Override
  public Optional<PostComment> findByCommentUuid(PostCommentUuid uuid) {
    return jpaPostCommentRepository.findByCommentUuid(uuid);
  }

  @Override
  public void deleteById(Integer id) {
    jpaPostCommentRepository.deleteById(id);
  }
}
