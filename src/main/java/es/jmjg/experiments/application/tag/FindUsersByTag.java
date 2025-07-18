package es.jmjg.experiments.application.tag;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.jmjg.experiments.application.tag.exception.TagNotFound;
import es.jmjg.experiments.domain.entity.User;
import es.jmjg.experiments.domain.repository.TagRepository;
import es.jmjg.experiments.domain.repository.UserRepository;

@Service
public class FindUsersByTag {

  private final UserRepository userRepository;
  private final TagRepository tagRepository;

  public FindUsersByTag(UserRepository userRepository, TagRepository tagRepository) {
    this.userRepository = userRepository;
    this.tagRepository = tagRepository;
  }

  @Transactional(readOnly = true)
  public List<User> findByTagUuid(UUID tagUuid) {
    var tag = tagRepository.findByUuid(tagUuid)
        .orElseThrow(() -> new TagNotFound(tagUuid));

    return userRepository.findByTagId(tag.getId());
  }

  @Transactional(readOnly = true)
  public List<User> findByTagName(String tagName) {
    if (tagName == null || tagName.trim().isEmpty()) {
      return List.of();
    }

    var tag = tagRepository.findByName(tagName.trim())
        .orElseThrow(() -> new TagNotFound("Tag not found with name: " + tagName));

    return userRepository.findByTagId(tag.getId());
  }
}