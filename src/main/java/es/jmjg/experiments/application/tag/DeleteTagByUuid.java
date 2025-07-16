package es.jmjg.experiments.application.tag;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.jmjg.experiments.application.tag.exception.TagInUseException;
import es.jmjg.experiments.application.tag.exception.TagNotFound;
import es.jmjg.experiments.infrastructure.repository.TagRepository;

@Service
public class DeleteTagByUuid {

  private final TagRepository tagRepository;

  public DeleteTagByUuid(TagRepository tagRepository) {
    this.tagRepository = tagRepository;
  }

  @Transactional
  public void deleteByUuid(UUID uuid) {
    var tag = tagRepository.findByUuid(uuid)
        .orElseThrow(() -> new TagNotFound(uuid));

    // Check if tag is used in posts
    if (tagRepository.isTagUsedInPosts(tag.getId())) {
      throw new TagInUseException(tag.getName(), uuid);
    }

    // Check if tag is used in users
    if (tagRepository.isTagUsedInUsers(tag.getId())) {
      throw new TagInUseException(tag.getName(), uuid);
    }

    tagRepository.deleteByUuid(uuid);
  }
}