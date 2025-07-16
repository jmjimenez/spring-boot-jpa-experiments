package es.jmjg.experiments.infrastructure.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import es.jmjg.experiments.domain.Post;
import jakarta.transaction.Transactional;

@Repository
@Transactional
public interface PostRepository extends JpaRepository<Post, Integer> {
  Optional<Post> findByTitle(String title);

  Optional<Post> findByUuid(UUID uuid);

  @Query("SELECT p FROM Post p WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(p.body) LIKE LOWER(CONCAT('%', :query, '%'))")
  List<Post> searchByContent(@Param("query") String query, Pageable pageable);

  @Query(value = "SELECT DISTINCT p.* FROM Post p JOIN post_tag pt ON p.id = pt.post_id WHERE pt.tag_id = :tagId", nativeQuery = true)
  List<Post> findByTagId(@Param("tagId") Integer tagId);
}
