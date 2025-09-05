package es.jmjg.experiments.application.post;

import es.jmjg.experiments.application.post.dto.UpdatePostTagsDto;
import es.jmjg.experiments.application.shared.dto.AuthenticatedUserDto;
import es.jmjg.experiments.application.post.exception.PostNotFound;
import es.jmjg.experiments.application.shared.exception.Forbidden;
import es.jmjg.experiments.domain.entity.Post;
import es.jmjg.experiments.domain.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdatePostTags {
    private final PostRepository postRepository;
    private final ProcessPostTags processPostTags;

    public UpdatePostTags(PostRepository postRepository, ProcessPostTags processPostTags) {
        this.postRepository = postRepository;
        this.processPostTags = processPostTags;
    }

    @Transactional
    public Post update(UpdatePostTagsDto dto) {
        Post post = postRepository.findByUuid(dto.postUuid())
            .orElseThrow(() -> new PostNotFound("Post not found"));

        AuthenticatedUserDto user = dto.authenticatedUserDto();
        boolean isOwner = post.getUser() != null && post.getUser().getUuid().equals(user.id());
        boolean isAdmin = user.isAdmin();
        if (!isOwner && !isAdmin) {
            throw new Forbidden("User is not authorized to update tags for this post");
        }

        processPostTags.processTagsForPost(post, dto.tagNames());
        return postRepository.save(post);
    }
}
