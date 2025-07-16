package es.jmjg.experiments.infrastructure.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import es.jmjg.experiments.domain.User;
import jakarta.transaction.Transactional;

@Repository
@Transactional
public interface UserRepository extends JpaRepository<User, Integer> {
  Optional<User> findByEmail(String email);

  Optional<User> findByUsername(String username);

  Optional<User> findByUuid(UUID uuid);

  void deleteByUuid(UUID uuid);

  @Query(value = "SELECT DISTINCT u.* FROM Users u JOIN user_tag ut ON u.id = ut.user_id WHERE ut.tag_id = :tagId", nativeQuery = true)
  List<User> findByTagId(@Param("tagId") Integer tagId);
}
