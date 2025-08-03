package es.jmjg.experiments.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import es.jmjg.experiments.domain.entity.Post;

//TODO: implement repositories as https://www.wimdeblauwe.com/blog/2025/07/30/how-i-test-production-ready-spring-boot-applications/
public interface PostRepository {

  void deleteById(Integer id);

  Page<Post> findAll(Pageable pageable);

  Optional<Post> findByTitle(String trim);

  Optional<Post> findByUuid(UUID uuid);

  List<Post> searchByContent(String trim, Pageable pageable);

  Post save(Post post);

  Optional<Post> findById(Integer id);

  List<Post> findByTagId(Integer id);
}
