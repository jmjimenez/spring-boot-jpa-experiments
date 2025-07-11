package es.jmjg.experiments.application.post;

import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import es.jmjg.experiments.domain.Post;
import es.jmjg.experiments.infrastructure.repository.PostRepository;

@Service
public class FindPostById {

    private final PostRepository postRepository;

    public FindPostById(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Transactional(readOnly = true)
    public Optional<Post> findById(Integer id) {
        if (id == null) {
            return Optional.empty();
        }
        return postRepository.findById(id);
    }
}
