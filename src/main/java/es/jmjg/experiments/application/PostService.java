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

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Transactional(readOnly = true)
    public List<Post> findAll() {
        return postRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Post> findById(Integer id) {
        return postRepository.findById(id);
    }

    @Transactional
    public Post save(Post post) {
        return postRepository.save(post);
    }

    @Transactional(readOnly = true)
    public Optional<Post> findByTitle(String title) {
        return postRepository.findByTitle(title);
    }

    @Transactional
    public void deleteById(Integer id) {
        postRepository.deleteById(id);
    }

    @Transactional
    public Post update(Integer id, Post post) {
        Optional<Post> existing = postRepository.findById(id);
        if (existing.isPresent()) {
            Post existingPost = existing.get();
            existingPost.setTitle(post.getTitle());
            existingPost.setBody(post.getBody());
            // Preserve the original user relationship
            return postRepository.save(existingPost);
        } else {
            throw new RuntimeException("Post not found with id: " + id);
        }
    }
}
