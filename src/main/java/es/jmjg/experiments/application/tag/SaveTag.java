package es.jmjg.experiments.application.tag;

import es.jmjg.experiments.domain.shared.exception.Forbidden;
import es.jmjg.experiments.application.tag.dto.SaveTagDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.jmjg.experiments.domain.tag.exception.TagAlreadyExistsException;
import es.jmjg.experiments.domain.tag.entity.Tag;
import es.jmjg.experiments.domain.tag.repository.TagRepository;

@Service
public class SaveTag {

  private final TagRepository tagRepository;

  public SaveTag(TagRepository tagRepository) {
    this.tagRepository = tagRepository;
  }

  @Transactional
  public Tag save(SaveTagDto dto) {
    tagRepository.findByUuid(dto.uuid()).ifPresent(t -> {
      throw new TagAlreadyExistsException(dto.uuid());
    });
    tagRepository.findByName(dto.tagName()).ifPresent(t -> {
      throw new TagAlreadyExistsException(dto.tagName(), t.getUuid());
    });

    if (!dto.authenticatedUser().isAdmin()) {
      throw new Forbidden("Only admins can create tags");
    }

    Tag tag = new Tag();
    tag.setUuid(dto.uuid());
    tag.setName(dto.tagName());
    return tagRepository.save(tag);
  }
}
