package es.jmjg.experiments.infrastructure.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import es.jmjg.experiments.domain.Tag;
import jakarta.transaction.Transactional;

@Repository
@Transactional
public interface TagRepository extends JpaRepository<Tag, Integer> {
  Optional<Tag> findByName(String name);

  Optional<Tag> findByUuid(UUID uuid);

  void deleteByUuid(UUID uuid);
}