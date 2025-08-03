package es.jmjg.experiments.infrastructure.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import es.jmjg.experiments.domain.entity.Tag;
import es.jmjg.experiments.infrastructure.repository.jpa.JpaTagRepository;

@Repository
public class TagRepositoryImpl implements es.jmjg.experiments.domain.repository.TagRepository {

  private final JpaTagRepository jpaTagRepository;

  public TagRepositoryImpl(JpaTagRepository jpaTagRepository) {
    this.jpaTagRepository = jpaTagRepository;
  }

  @Override
  public Optional<Tag> findByUuid(UUID uuid) {
    return jpaTagRepository.findByUuid(uuid);
  }

  @Override
  public void deleteByUuid(UUID uuid) {
    jpaTagRepository.deleteByUuid(uuid);
  }

  @Override
  public boolean isTagUsedInPosts(Integer id) {
    return jpaTagRepository.isTagUsedInPosts(id);
  }

  @Override
  public boolean isTagUsedInUsers(Integer id) {
    return jpaTagRepository.isTagUsedInUsers(id);
  }

  @Override
  public Optional<Tag> findByName(String trim) {
    return jpaTagRepository.findByName(trim);
  }

  @Override
  public List<Tag> findByNameContainingPattern(String trim) {
    return jpaTagRepository.findByNameContainingPattern(trim);
  }

  @Override
  public Tag save(Tag tag) {
    return jpaTagRepository.save(tag);
  }

  public Optional<Tag> findById(Integer id) {
    return jpaTagRepository.findById(id);
  }
}
