package es.jmjg.experiments.application.post;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import es.jmjg.experiments.domain.entity.Post;
import es.jmjg.experiments.domain.entity.Tag;
import es.jmjg.experiments.domain.repository.TagRepository;

//TODO: test this service
@Service
public class ProcessPostTags {

  private final TagRepository tagRepository;

  public ProcessPostTags(TagRepository tagRepository) {
    this.tagRepository = tagRepository;
  }

  @Transactional
  public void processTagsForPost(Post post, @NotNull List<String> tagNames) {
    if (tagNames == null) {
      return;
    }

    List<Tag> processedTags = new ArrayList<>();

    for (String tagName : tagNames) {
      if (tagName == null || tagName.trim().isEmpty()) {
        continue;
      }

      String trimmedTagName = tagName.trim();
      Optional<Tag> existingTag = tagRepository.findByName(trimmedTagName);

      if (existingTag.isPresent()) {
        processedTags.add(existingTag.get());
      } else {
        Tag newTag = new Tag();
        newTag.setUuid(UUID.randomUUID());
        newTag.setName(trimmedTagName);

        Tag savedTag = tagRepository.save(newTag);
        processedTags.add(savedTag);
      }
    }

    post.setTags(processedTags);
  }
}
