package es.jmjg.experiments.infrastructure.repository.jpa;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import es.jmjg.experiments.domain.post.entity.Post;

public interface JpaPostRepository extends JpaRepository<Post, Integer> {

  @Transactional(readOnly = true)
  Optional<Post> findByUuid(UUID uuid);

  @Transactional(readOnly = true)
  @Query("SELECT p FROM Post p LEFT JOIN FETCH p.user WHERE p.title = :title")
  Optional<Post> findByTitle(@Param("title") String title);

  @Transactional(readOnly = true)
  @Query("SELECT p FROM Post p WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(p.body) LIKE LOWER(CONCAT('%', :query, '%'))")
  List<Post> searchByContent(@Param("query") String query, Pageable pageable);

  @Transactional(readOnly = true)
  @Query(value = "SELECT DISTINCT p.* FROM post p JOIN post_tag pt ON p.id = pt.post_id WHERE pt.tag_id = :tagId", nativeQuery = true)
  List<Post> findByTagId(@Param("tagId") Integer tagId);

  @Transactional(readOnly = true)
  List<Post> findByUserId(Integer userId);
}
