package es.jmjg.experiments.application.tag;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.jmjg.experiments.application.tag.exception.TagNotFound;
import es.jmjg.experiments.domain.Tag;
import es.jmjg.experiments.infrastructure.repository.TagRepository;

@Service
public class UpdateTagName {

  private final TagRepository tagRepository;

  public UpdateTagName(TagRepository tagRepository) {
    this.tagRepository = tagRepository;
  }

  @Transactional
  public Tag updateName(UUID uuid, String newName) {
    if (newName == null || newName.trim().isEmpty()) {
      throw new IllegalArgumentException("Tag name cannot be null or empty");
    }

    var tag = tagRepository.findByUuid(uuid)
        .orElseThrow(() -> new TagNotFound(uuid));

    tag.setName(newName.trim());
    return tagRepository.save(tag);
  }
}