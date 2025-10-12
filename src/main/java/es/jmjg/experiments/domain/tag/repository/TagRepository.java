package es.jmjg.experiments.domain.tag.repository;

import es.jmjg.experiments.domain.tag.entity.TagUuid;
import java.util.List;
import java.util.Optional;

import es.jmjg.experiments.domain.tag.entity.Tag;

public interface TagRepository {

  Optional<Tag> findByTagUuid(TagUuid uuid);

  void deleteByTagUuid(TagUuid uuid);

  Optional<Tag> findByName(String trim);

  List<Tag> findByNameContainingPattern(String trim);

  Tag save(Tag tag);
}
