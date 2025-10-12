package es.jmjg.experiments.application.tag;

import es.jmjg.experiments.domain.tag.entity.TagUuid;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.jmjg.experiments.domain.tag.exception.TagNotFound;
import es.jmjg.experiments.domain.tag.entity.Tag;
import es.jmjg.experiments.domain.tag.repository.TagRepository;

@Service
public class FindTagByUuid {

  private final TagRepository tagRepository;

  public FindTagByUuid(TagRepository tagRepository) {
    this.tagRepository = tagRepository;
  }

  @Transactional(readOnly = true)
  public Tag findByTagUuid(TagUuid uuid) {
    if (uuid == null) {
      throw new IllegalArgumentException("UUID cannot be null");
    }
    return tagRepository.findByTagUuid(uuid)
        .orElseThrow(() -> new TagNotFound(uuid));
  }
}
