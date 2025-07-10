package es.jmjg.experiments.application;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import es.jmjg.experiments.domain.Post;
import es.jmjg.experiments.infrastructure.repository.PostRepository;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final FindAllPosts findAllPosts;

    public PostService(PostRepository postRepository, FindAllPosts findAllPosts) {
        this.postRepository = postRepository;
        this.findAllPosts = findAllPosts;
    }

    @Transactional(readOnly = true)
    public List<Post> findAll() {
        return findAllPosts.findAll();
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
