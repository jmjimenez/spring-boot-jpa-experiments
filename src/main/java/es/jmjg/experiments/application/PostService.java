package es.jmjg.experiments.application;

import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import es.jmjg.experiments.domain.Post;
import es.jmjg.experiments.infrastructure.repository.PostRepository;

@Service
public class PostService {

    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Transactional(readOnly = true)
    public Optional<Post> findByTitle(String title) {
        return postRepository.findByTitle(title);
    }

    @Transactional
    public void deleteById(Integer id) {
        postRepository.deleteById(id);
    }
}
