package es.jmjg.experiments.application;

import es.jmjg.experiments.domain.Post;
import es.jmjg.experiments.infrastructure.repository.PostRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PostService {

    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public List<Post> findAll() {
        return postRepository.findAll();
    }

    public Optional<Post> findById(Integer id) {
        return postRepository.findById(id);
    }

    public Post save(Post post) {
        return postRepository.save(post);
    }

    public Optional<Post> findByTitle(String title) {
        return postRepository.findByTitle(title);
    }

    public void deleteById(Integer id) {
        postRepository.deleteById(id);
    }

    public Post update(Integer id, Post post) {
        Optional<Post> existing = postRepository.findById(id);
        if (existing.isPresent()) {
            Post updatedPost = new Post(
                existing.get().getId(),
                existing.get().getUserId(),
                post.getTitle(),
                post.getBody()
            );
            return postRepository.save(updatedPost);
        } else {
            throw new RuntimeException("Post not found with id: " + id);
        }
    }
}
