package es.jmjg.experiments.infrastructure.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import es.jmjg.experiments.domain.entity.User;
import es.jmjg.experiments.infrastructure.repository.jpa.JpaUserRepository;

@Repository
@Transactional(readOnly = true)
public class UserRepositoryImpl implements es.jmjg.experiments.domain.repository.UserRepository {

  private final JpaUserRepository jpaUserRepository;

  public UserRepositoryImpl(JpaUserRepository jpaUserRepository) {
    this.jpaUserRepository = jpaUserRepository;
  }

  @Override
  @Transactional
  public void deleteByUuid(UUID uuid) {
    jpaUserRepository.deleteByUuid(uuid);
  }

  @Override
  public Page<User> findAll(Pageable pageable) {
    return jpaUserRepository.findAll(pageable);
  }

  @Override
  public Optional<User> findByEmail(String email) {
    return jpaUserRepository.findByEmail(email);
  }

  @Override
  public Optional<User> findById(Integer id) {
    return jpaUserRepository.findById(id);
  }

  @Override
  public Optional<User> findByUsername(String username) {
    return jpaUserRepository.findByUsername(username);
  }

  @Override
  public Optional<User> findByUuid(UUID uuid) {
    return jpaUserRepository.findByUuid(uuid);
  }

  @Override
  @Transactional
  public User save(User existingUser) {
    return jpaUserRepository.save(existingUser);
  }

  @Override
  public List<User> findByTagId(Integer id) {
    return jpaUserRepository.findByTagId(id);
  }

  public Long count() {
    return jpaUserRepository.count();
  }

  @Transactional
  public void deleteAll() {
    jpaUserRepository.deleteAll();
  }

  @Transactional
  public void deleteById(Integer id) {
    jpaUserRepository.deleteById(id);
  }
}
