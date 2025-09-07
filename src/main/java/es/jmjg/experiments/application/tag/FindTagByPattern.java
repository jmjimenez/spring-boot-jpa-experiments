package es.jmjg.experiments.application.tag;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.jmjg.experiments.domain.tag.entity.Tag;
import es.jmjg.experiments.domain.tag.repository.TagRepository;

@Service
public class FindTagByPattern {

  private final TagRepository tagRepository;

  public FindTagByPattern(TagRepository tagRepository) {
    this.tagRepository = tagRepository;
  }

  @Transactional(readOnly = true)
  public List<Tag> findByPattern(String pattern) {
    if (pattern == null || pattern.trim().isEmpty()) {
      return List.of();
    }
    return tagRepository.findByNameContainingPattern(pattern.trim());
  }
}
