package es.jmjg.experiments.application.tag;

import org.springframework.dao.DataIntegrityViolationException;
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
    try {
      return tagRepository.save(tag);
    } catch (DataIntegrityViolationException e) {
      // Check if it's a duplicate UUID or name constraint violation
      String errorMessage = e.getMessage();
      if (errorMessage != null) {
        if (errorMessage.contains("uuid") || errorMessage.contains("UUID")) {
          throw new TagAlreadyExistsException(tag.getUuid());
        } else if (errorMessage.contains("tag") || errorMessage.contains("name")) {
          throw new TagAlreadyExistsException(tag.getName(), tag.getUuid());
        }
      }
      // Generic message if we can't determine the specific constraint
      throw new TagAlreadyExistsException("Tag already exists");
    }
  }
}