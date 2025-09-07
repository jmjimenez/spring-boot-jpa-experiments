package es.jmjg.experiments.application.tag;

import es.jmjg.experiments.application.tag.dto.DeleteTagDto;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.jmjg.experiments.domain.tag.repository.TagRepository;

@Service
public class DeleteTag {

  private final TagRepository tagRepository;

  public DeleteTag(TagRepository tagRepository) {
    this.tagRepository = tagRepository;
  }

  @Transactional
  public void delete(DeleteTagDto deleteTagDto) {
    tagRepository.deleteByUuid(deleteTagDto.uuid());
  }
}
