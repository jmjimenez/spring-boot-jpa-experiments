package es.jmjg.experiments.application.tag;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.jmjg.experiments.domain.tag.exception.TagNotFound;
import es.jmjg.experiments.domain.post.entity.Post;
import es.jmjg.experiments.domain.post.repository.PostRepository;
import es.jmjg.experiments.domain.tag.repository.TagRepository;

@Service
public class FindPostsByTag {

  private final PostRepository postRepository;
  private final TagRepository tagRepository;

  public FindPostsByTag(PostRepository postRepository, TagRepository tagRepository) {
    this.postRepository = postRepository;
    this.tagRepository = tagRepository;
  }

  @Transactional(readOnly = true)
  public List<Post> findByTagUuid(UUID tagUuid) {
    var tag = tagRepository.findByUuid(tagUuid)
        .orElseThrow(() -> new TagNotFound(tagUuid));

    return postRepository.findByTagId(tag.getId());
  }

  @Transactional(readOnly = true)
  public List<Post> findByTagName(String tagName) {
    if (tagName == null || tagName.trim().isEmpty()) {
      return List.of();
    }

    var tag = tagRepository.findByName(tagName.trim())
        .orElseThrow(() -> new TagNotFound("Tag not found with name: " + tagName));

    return postRepository.findByTagId(tag.getId());
  }
}
