package es.jmjg.experiments.infrastructure.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import es.jmjg.experiments.domain.Post;
import jakarta.transaction.Transactional;

@Repository
@Transactional
public interface PostRepository extends JpaRepository<Post, Integer> {
    Optional<Post> findByTitle(String title);
}
