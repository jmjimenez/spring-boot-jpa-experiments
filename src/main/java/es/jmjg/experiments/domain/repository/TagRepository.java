package es.jmjg.experiments.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import es.jmjg.experiments.domain.entity.Tag;

public interface TagRepository {

  Optional<Tag> findByUuid(UUID uuid);

  void deleteByUuid(UUID uuid);

  boolean isTagUsedInPosts(Integer id);

  boolean isTagUsedInUsers(Integer id);

  Optional<Tag> findByName(String trim);

  List<Tag> findByNameContainingPattern(String trim);

  Tag save(Tag tag);
}
