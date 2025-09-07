package es.jmjg.experiments.infrastructure.repository;

import es.jmjg.experiments.domain.tag.repository.TagRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import es.jmjg.experiments.domain.tag.entity.Tag;
import es.jmjg.experiments.infrastructure.repository.jpa.JpaTagRepository;

@Repository
@Transactional(readOnly = true)
public class TagRepositoryImpl implements TagRepository {

  private final JpaTagRepository jpaTagRepository;

  public TagRepositoryImpl(JpaTagRepository jpaTagRepository) {
    this.jpaTagRepository = jpaTagRepository;
  }

  @Override
  public Optional<Tag> findByUuid(UUID uuid) {
    return jpaTagRepository.findByUuid(uuid);
  }

  @Override
  @Transactional
  public void deleteByUuid(UUID uuid) {
    jpaTagRepository.deleteByUuid(uuid);
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
  @Transactional
  public Tag save(Tag tag) {
    return jpaTagRepository.save(tag);
  }

  public Optional<Tag> findById(Integer id) {
    return jpaTagRepository.findById(id);
  }
}
