package es.jmjg.experiments.infrastructure.repository.jpa;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import es.jmjg.experiments.domain.entity.User;

public interface JpaUserRepository extends JpaRepository<User, Integer> {

  @Transactional(readOnly = true)
  Optional<User> findByEmail(String email);

  @Transactional(readOnly = true)
  Optional<User> findByUsername(String username);

  @Transactional(readOnly = true)
  Optional<User> findByUuid(UUID uuid);

  @Transactional
  void deleteByUuid(UUID uuid);

  @Transactional(readOnly = true)
  @Query(value = "SELECT DISTINCT u.* FROM Users u JOIN user_tag ut ON u.id = ut.user_id WHERE ut.tag_id = :tagId", nativeQuery = true)
  List<User> findByTagId(@Param("tagId") Integer tagId);
}
