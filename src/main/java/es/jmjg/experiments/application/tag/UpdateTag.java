package es.jmjg.experiments.application.tag;

import es.jmjg.experiments.domain.shared.exception.Forbidden;
import es.jmjg.experiments.application.tag.dto.UpdateTagDto;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.jmjg.experiments.domain.tag.exception.TagNotFound;
import es.jmjg.experiments.domain.tag.entity.Tag;
import es.jmjg.experiments.domain.tag.repository.TagRepository;

@Service
public class UpdateTag {

  private final TagRepository tagRepository;

  public UpdateTag(TagRepository tagRepository) {
    this.tagRepository = tagRepository;
  }

  @Transactional
  public Tag update(UpdateTagDto dto) {
    if (dto.tagName() == null || dto.tagName().trim().isEmpty()) {
      throw new IllegalArgumentException("Tag name cannot be null or empty");
    }

    if (!dto.authenticatedUser().isAdmin()) {
      throw new Forbidden("Only admins can update tags");
    }

    var tag = tagRepository.findByUuid(dto.uuid())
        .orElseThrow(() -> new TagNotFound(dto.uuid()));

    tag.setName(dto.tagName().trim());
    return tagRepository.save(tag);
  }
}
