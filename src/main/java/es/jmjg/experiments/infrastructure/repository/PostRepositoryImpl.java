package es.jmjg.experiments.infrastructure.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import es.jmjg.experiments.domain.entity.Post;
import es.jmjg.experiments.infrastructure.repository.jpa.JpaPostRepository;

@Repository
@Transactional(readOnly = true)
public class PostRepositoryImpl implements es.jmjg.experiments.domain.repository.PostRepository {

  private final JpaPostRepository jpaPostRepository;

  public PostRepositoryImpl(JpaPostRepository jpaPostRepository) {
    this.jpaPostRepository = jpaPostRepository;
  }

  @Override
  @Transactional
  public void deleteById(Integer id) {
    jpaPostRepository.deleteById(id);
  }

  @Override
  public Page<Post> findAll(Pageable pageable) {
    return jpaPostRepository.findAll(pageable);
  }

  @Override
  public Optional<Post> findByTitle(String trim) {
    return jpaPostRepository.findByTitle(trim);
  }

  @Override
  public Optional<Post> findByUuid(UUID uuid) {
    return jpaPostRepository.findByUuid(uuid);
  }

  @Override
  public List<Post> searchByContent(String trim, Pageable pageable) {
    return jpaPostRepository.searchByContent(trim, pageable);
  }

  @Override
  @Transactional
  public Post save(Post post) {
    return jpaPostRepository.save(post);
  }

  @Override
  public Optional<Post> findById(Integer id) {
    return jpaPostRepository.findById(id);
  }

  @Override
  public List<Post> findByTagId(Integer id) {
    return jpaPostRepository.findByTagId(id);
  }

  @Override
  public List<Post> findByUserId(Integer userId) {
    return jpaPostRepository.findByUserId(userId);
  }

  public Long count() {
    return jpaPostRepository.count();
  }

}
