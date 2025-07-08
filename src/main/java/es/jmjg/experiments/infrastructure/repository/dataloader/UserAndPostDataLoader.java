package es.jmjg.experiments.infrastructure.repository.dataloader;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.jmjg.experiments.domain.Post;
import es.jmjg.experiments.domain.User;
import es.jmjg.experiments.infrastructure.repository.PostRepository;
import es.jmjg.experiments.infrastructure.repository.UserRepository;

@Component
@Profile({"dev", "test"})
class UserAndPostDataLoader implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(UserAndPostDataLoader.class);
    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    public UserAndPostDataLoader(ObjectMapper objectMapper, UserRepository userRepository,
            PostRepository postRepository) {
        this.objectMapper = objectMapper;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
    }

    @Override
    public void run(String... args) {
        try {
            if (userRepository.count() == 0 && postRepository.count() == 0) {
                String USERS_AND_POSTS_JSON = "/data/users-and-posts.json";
                log.info("Loading users and posts into database from JSON: {}",
                        USERS_AND_POSTS_JSON);
                try (InputStream inputStream =
                        TypeReference.class.getResourceAsStream(USERS_AND_POSTS_JSON)) {
                    if (inputStream == null) {
                        log.warn("Could not find JSON data file: {}", USERS_AND_POSTS_JSON);
                        return;
                    }

                    UsersWithPosts response =
                            objectMapper.readValue(inputStream, UsersWithPosts.class);
                    List<UserWithPosts> usersWithPosts = response.users();

                    for (UserWithPosts userWithPosts : usersWithPosts) {
                        // Create and save the user
                        User user = new User();
                        user.setId(null); // Let Hibernate generate the ID
                        user.setName(userWithPosts.name());
                        user.setEmail(userWithPosts.email());
                        user.setUsername(userWithPosts.username());

                        User savedUser = userRepository.save(user);

                        // Create and save the posts for this user
                        List<PostData> postDataList = userWithPosts.posts();
                        for (PostData postData : postDataList) {
                            Post post = new Post();
                            post.setId(null); // Let Hibernate generate the ID
                            post.setUuid(UUID.fromString(postData.id())); // Map JSON "id" to entity
                                                                          // "uuid"
                            post.setUser(savedUser); // Set the relationship
                            post.setTitle(postData.title());
                            post.setBody(postData.body());

                            postRepository.save(post);
                        }
                    }

                    log.info("Successfully loaded {} users and their posts", usersWithPosts.size());
                } catch (IOException e) {
                    log.error("Failed to read JSON data", e);
                    // Don't throw RuntimeException to avoid context loading failure
                }
            }
        } catch (Exception e) {
            log.error("Error during data loading", e);
            // Don't throw RuntimeException to avoid context loading failure
        }
    }

    // Static inner classes for JSON deserialization
    public static record UserWithPosts(Integer id, String name, String email, String username,
            List<PostData> posts) {
    }

    public static record UsersWithPosts(List<UserWithPosts> users) {
    }

    // Separate record for post data from JSON (to avoid conflicts with entity structure)
    public static record PostData(String id, String title, String body) {
    }
}
