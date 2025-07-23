package es.jmjg.experiments.application.tag;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.jmjg.experiments.application.tag.exception.TagAlreadyExistsException;
import es.jmjg.experiments.domain.entity.Tag;
import es.jmjg.experiments.domain.repository.TagRepository;

@Service
public class SaveTag {

  private final TagRepository tagRepository;

  public SaveTag(TagRepository tagRepository) {
    this.tagRepository = tagRepository;
  }

  @Transactional
  public Tag save(Tag tag) {
    tagRepository.findByUuid(tag.getUuid()).ifPresent(t -> {
      throw new TagAlreadyExistsException(tag.getUuid());
    });
    tagRepository.findByName(tag.getName()).ifPresent(t -> {
      throw new TagAlreadyExistsException(tag.getName(), t.getUuid());
    });
    return tagRepository.save(tag);
  }
}