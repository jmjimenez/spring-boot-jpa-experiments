package es.jmjg.experiments.application;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import es.jmjg.experiments.application.exception.PostNotFound;
import es.jmjg.experiments.application.exception.UserNotFound;
import es.jmjg.experiments.domain.Post;
import es.jmjg.experiments.domain.User;
import es.jmjg.experiments.infrastructure.repository.PostRepository;
import es.jmjg.experiments.infrastructure.repository.UserRepository;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public PostService(PostRepository postRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<Post> findAll() {
        return postRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Post> findById(Integer id) {
        return postRepository.findById(id);
    }

    public Post save(Post post) {
        return save(post, null);
    }

    @Transactional
    public Post save(Post post, Integer userId) {
        // If the post has a userId but no user relationship, set up the relationship
        if (userId != null) {
            Optional<User> user = userRepository.findById(userId);
            if (user.isPresent()) {
                post.setUser(user.get());
            } else {
                throw new UserNotFound(userId);
            }
        }
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

    public Post update(Integer id, Post post) {
        return update(id, post, null);
    }

    @Transactional
    public Post update(Integer id, Post post, Integer userId) {
        Optional<Post> existing = postRepository.findById(id);
        if (existing.isEmpty()) {
            throw new PostNotFound(id);
        }

        Post existingPost = existing.get();
        existingPost.setTitle(post.getTitle());
        existingPost.setBody(post.getBody());

        if (userId != null) {
            Optional<User> user = userRepository.findById(userId);
            if (user.isPresent()) {
                existingPost.setUser(user.get());
            } else {
                throw new UserNotFound(userId);
            }
        } else if (post.getUser() != null) {
            existingPost.setUser(post.getUser());
        }

        return postRepository.save(existingPost);
    }
}
