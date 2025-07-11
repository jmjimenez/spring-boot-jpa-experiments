package es.jmjg.experiments.infrastructure.controller;

import static org.mockito.Mockito.*;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import es.jmjg.experiments.application.post.DeletePostById;
import es.jmjg.experiments.application.post.FindAllPosts;
import es.jmjg.experiments.application.post.FindPostById;
import es.jmjg.experiments.application.post.FindPosts;
import es.jmjg.experiments.application.post.SavePost;
import es.jmjg.experiments.application.post.UpdatePost;
import es.jmjg.experiments.infrastructure.controller.mapper.PostMapper;

@TestConfiguration
public class ControllerTestConfig {

    @Bean
    @Primary
    public DeletePostById deletePostById() {
        return mock(DeletePostById.class);
    }

    @Bean
    @Primary
    public FindPosts findPosts() {
        return mock(FindPosts.class);
    }

    @Bean
    @Primary
    public UpdatePost updatePost() {
        return mock(UpdatePost.class);
    }

    @Bean
    @Primary
    public SavePost savePost() {
        return mock(SavePost.class);
    }

    @Bean
    @Primary
    public FindPostById findPostById() {
        return mock(FindPostById.class);
    }

    @Bean
    @Primary
    public FindAllPosts findAllPosts() {
        return mock(FindAllPosts.class);
    }

    @Bean
    public PostMapper postMapper() {
        return new PostMapper();
    }
}
