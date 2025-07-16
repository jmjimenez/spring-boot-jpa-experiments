package es.jmjg.experiments.infrastructure.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import es.jmjg.experiments.domain.Tag;
import jakarta.transaction.Transactional;

@Repository
@Transactional
public interface TagRepository extends JpaRepository<Tag, Integer> {
  Optional<Tag> findByName(String name);

  Optional<Tag> findByUuid(UUID uuid);

  void deleteByUuid(UUID uuid);

  @Query("SELECT t FROM Tag t WHERE LOWER(t.name) LIKE LOWER(CONCAT('%', :pattern, '%'))")
  List<Tag> findByNameContainingPattern(@Param("pattern") String pattern);

  @Query(value = "SELECT COUNT(pt) > 0 FROM post_tag pt WHERE pt.tag_id = :tagId", nativeQuery = true)
  boolean isTagUsedInPosts(@Param("tagId") Integer tagId);

  @Query(value = "SELECT COUNT(ut) > 0 FROM user_tag ut WHERE ut.tag_id = :tagId", nativeQuery = true)
  boolean isTagUsedInUsers(@Param("tagId") Integer tagId);
}