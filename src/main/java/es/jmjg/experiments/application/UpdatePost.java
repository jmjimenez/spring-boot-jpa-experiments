package es.jmjg.experiments.application;

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
public class UpdatePost {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public UpdatePost(PostRepository postRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
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

        // Only update UUID if provided (to avoid overwriting existing UUID)
        if (post.getUuid() != null) {
            existingPost.setUuid(post.getUuid());
        }

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
