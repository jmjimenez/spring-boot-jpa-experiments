package es.jmjg.experiments.domain.tag.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import es.jmjg.experiments.domain.tag.entity.Tag;

public interface TagRepository {

  Optional<Tag> findByUuid(UUID uuid);

  void deleteByUuid(UUID uuid);

  Optional<Tag> findByName(String trim);

  List<Tag> findByNameContainingPattern(String trim);

  Tag save(Tag tag);
}
