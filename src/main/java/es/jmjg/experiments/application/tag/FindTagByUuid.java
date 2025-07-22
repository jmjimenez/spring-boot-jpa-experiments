package es.jmjg.experiments.application.tag;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.jmjg.experiments.application.tag.exception.TagNotFound;
import es.jmjg.experiments.domain.entity.Tag;
import es.jmjg.experiments.domain.repository.TagRepository;

@Service
public class FindTagByUuid {

  private final TagRepository tagRepository;

  public FindTagByUuid(TagRepository tagRepository) {
    this.tagRepository = tagRepository;
  }

  @Transactional(readOnly = true)
  public Tag findByUuid(UUID uuid) {
    if (uuid == null) {
      throw new IllegalArgumentException("UUID cannot be null");
    }
    return tagRepository.findByUuid(uuid)
        .orElseThrow(() -> new TagNotFound(uuid));
  }

  @Transactional(readOnly = true)
  public Optional<Tag> findByUuidOptional(UUID uuid) {
    if (uuid == null) {
      return Optional.empty();
    }
    return tagRepository.findByUuid(uuid);
  }
}