package es.jmjg.experiments.infrastructure.repository;

import es.jmjg.experiments.domain.Post;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Transactional
public interface PostRepository extends JpaRepository<Post,Integer> {
    Optional<Post> findByTitle(String title);
}