package es.jmjg.experiments.application.tag;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.jmjg.experiments.application.tag.exception.TagInUseException;
import es.jmjg.experiments.domain.repository.TagRepository;

@Service
public class DeleteTagByUuid {

  private final TagRepository tagRepository;

  public DeleteTagByUuid(TagRepository tagRepository) {
    this.tagRepository = tagRepository;
  }

  @Transactional
  public void deleteByUuid(UUID uuid) {
    try {
      tagRepository.deleteByUuid(uuid);
    } catch (es.jmjg.experiments.domain.exception.TagInUseException e) {
      throw new TagInUseException(e.getMessage());
    }
  }
}