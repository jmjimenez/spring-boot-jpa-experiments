package es.jmjg.experiments.infrastructure.repository.jpa;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import es.jmjg.experiments.application.tag.exception.TagNotFound;
import es.jmjg.experiments.domain.entity.Tag;
import es.jmjg.experiments.domain.exception.TagInUseException;

public interface JpaTagRepository extends JpaRepository<Tag, Integer> {

  @Transactional(readOnly = true)
  Optional<Tag> findByUuid(UUID uuid);

  @Transactional(readOnly = true)
  Optional<Tag> findByName(String name);

  @Transactional
  default void deleteByUuid(UUID uuid) {
    Tag tag = findByUuid(uuid)
        .orElseThrow(() -> new TagNotFound(uuid));

    if (isTagUsedInPosts(tag.getId())) {
      throw new TagInUseException(
          "Cannot delete tag '" + tag.getName() + "' because it is assigned to posts");
    }

    if (isTagUsedInUsers(tag.getId())) {
      throw new TagInUseException(
          "Cannot delete tag '" + tag.getName() + "' because it is assigned to users");
    }

    delete(tag);
  }

  @Transactional(readOnly = true)
  @Query("SELECT t FROM Tag t WHERE LOWER(t.name) LIKE LOWER(CONCAT('%', :pattern, '%'))")
  List<Tag> findByNameContainingPattern(@Param("pattern") String pattern);

  @Transactional(readOnly = true)
  @Query(value = "SELECT COUNT(pt) > 0 FROM post_tag pt WHERE pt.tag_id = :tagId", nativeQuery = true)
  boolean isTagUsedInPosts(@Param("tagId") Integer tagId);

  @Transactional(readOnly = true)
  @Query(value = "SELECT COUNT(ut) > 0 FROM user_tag ut WHERE ut.tag_id = :tagId", nativeQuery = true)
  boolean isTagUsedInUsers(@Param("tagId") Integer tagId);
}