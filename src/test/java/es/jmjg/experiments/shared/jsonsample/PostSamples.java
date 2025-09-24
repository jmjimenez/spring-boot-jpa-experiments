package es.jmjg.experiments.shared.jsonsample;

import java.util.List;

import es.jmjg.experiments.domain.post.entity.Post;

/**
 * Utility class for creating JSON samples used in tests.
 * Contains methods for generating JSON strings for various API responses and
 * requests.
 */
public final class PostSamples {

  private PostSamples() {
    // Utility class - prevent instantiation
  }

  public static String createFindAllPostsJsonResponse(List<Post> posts) {
    return """
        {
            "content":[
                {
                    "id":"%s",
                    "user-id":"%s",
                    "title":"Hello, World!",
                    "body":"This is my first post.",
                    "tags":[]
                },
                {
                    "id":"%s",
                    "user-id":"%s",
                    "title":"Second Post",
                    "body":"This is my second post.",
                    "tags":[]
                }
            ],
            "pageNumber":0,
            "pageSize":20,
            "totalElements":2,
            "totalPages":1,
            "hasNext":false,
            "hasPrevious":false
        }
        """
      .formatted(posts.get(0).getUuid(), posts.get(0).getUser().getUuid(), posts.get(1).getUuid(),
        posts.get(1).getUser().getUuid());
  }

  public static String createFindAllPostsWithPaginationJsonResponse(List<Post> posts) {
    return """
        {
            "content":[
                {
                    "id":"%s",
                    "user-id":"%s",
                    "title":"Hello, World!",
                    "body":"This is my first post.",
                    "tags":[]
                }
            ],
            "pageNumber":0,
            "pageSize":1,
            "totalElements":2,
            "totalPages":2,
            "hasNext":true,
            "hasPrevious":false
        }
        """
      .formatted(posts.getFirst().getUuid(), posts.getFirst().getUser().getUuid());
  }

  public static String createEmptyPostsJsonResponse() {
    return """
        {
            "content":[],
            "pageNumber":0,
            "pageSize":20,
            "totalElements":0,
            "totalPages":0,
            "hasNext":false,
            "hasPrevious":false
        }
        """;
  }

  public static String createFindPostByUuidJsonResponse(Post post) {
    String tags = post.getTags().stream()
      .map(tag -> "{\"id\":\"%s\",\"name\":\"%s\"}".formatted(tag.getUuid(), tag.getName()))
      .reduce((a, b) -> a + "," + b)
      .orElse("");

    String comments = post.getComments().stream()
      .map(comment -> "{\"id\":\"%s\"}".formatted(comment.getUuid()))
      .reduce((a, b) -> a + "," + b)
      .orElse("");

    return """
        {
            "id":"%s",
            "user-id":"%s",
            "title":"%s",
            "body":"%s",
            "tags":[%s],
            "post-comments":[%s]
        }
        """
      .formatted(
        post.getUuid(),
        post.getUser().getUuid(),
        post.getTitle(),
        post.getBody(),
        tags,
        comments);
  }

  public static String createCreatePostRequestJson(Post post) {
    return """
        {
            "id":"%s",
            "title":"%s",
            "body":"%s"
        }
        """
      .formatted(post.getUuid(), post.getTitle(), post.getBody());
  }

  public static String createCreatePostResponseJson(Post post) {
    return """
        {
            "id":"%s",
            "user-id":"%s",
            "title":"%s",
            "body":"%s",
            "tags":[]
        }
        """
      .formatted(
        post.getUuid(),
        post.getUser().getUuid(),
        post.getTitle(),
        post.getBody());
  }

  public static String createUpdatePostRequestJson(Post post) {
    return """
        {
            "id":"%s",
            "title":"%s",
            "body":"%s"
        }
        """
      .formatted(
        post.getUuid(),
        post.getTitle(),
        post.getBody());
  }

  public static String createSearchPostsJsonResponse(List<Post> searchResults) {
    return """
        [
            {
                "id":"%s",
                "title":"%s",
                "body":"%s",
                "tags":[]
            },
            {
                "id":"%s",
                "title":"%s",
                "body":"%s",
                "tags":[]
            }
        ]
        """
      .formatted(
        searchResults.get(0).getUuid(),
        searchResults.get(0).getTitle(),
        searchResults.get(0).getBody(),
        searchResults.get(1).getUuid(),
        searchResults.get(1).getTitle(),
        searchResults.get(1).getBody());
  }

  public static String createUpdatePostTagsRequestJson(es.jmjg.experiments.infrastructure.controller.post.dto.UpdatePostTagsRequestDto dto) {
    try {
      return new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(dto);
    } catch (Exception e) {
      throw new RuntimeException("Failed to serialize UpdatePostTagsRequestDto", e);
    }
  }

}
