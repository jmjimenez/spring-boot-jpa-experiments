package es.jmjg.experiments.infrastructure.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import es.jmjg.experiments.domain.Post;
import jakarta.transaction.Transactional;

@Repository
@Transactional
public interface PostRepository extends JpaRepository<Post, Integer> {
    Optional<Post> findByTitle(String title);
    
    @Query("SELECT p FROM Post p WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(p.body) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Post> searchByContent(@Param("query") String query, Pageable pageable);
}
