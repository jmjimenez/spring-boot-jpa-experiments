package es.jmjg.experiments.shared;

import es.jmjg.experiments.domain.post.entity.PostComment;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import es.jmjg.experiments.domain.post.entity.Post;

/**
 * Utility class for creating JSON samples used in tests.
 * Contains methods for generating JSON strings for various API responses and
 * requests.
 */
public final class JsonSamples {

  private JsonSamples() {
    // Utility class - prevent instantiation
  }

  public static String createFindAllPostsJsonResponse(List<Post> posts) {
    return """
        {
            "content":[
                {
                    "id":"%s",
                    "userId":"%s",
                    "title":"Hello, World!",
                    "body":"This is my first post.",
                    "tags":[]
                },
                {
                    "id":"%s",
                    "userId":"%s",
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
                    "userId":"%s",
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
            "userId":"%s",
            "title":"%s",
            "body":"%s",
            "tags":[%s],
            "postComments":[%s]
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
            "userId":"%s",
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

  public static String createFindAllUsersJsonResponse(List<Post> testPosts, UUID testUuid) {
    return """
        {
            "content":[
                {
                    "id":"%s",
                    "name":"Test User",
                    "email":"test@example.com",
                    "username":"testuser",
                    "posts":["%s","%s"],
                    "tags":["technology","java"]
                }
            ],
            "pageable":{
                "sort":{
                    "empty":true,
                    "sorted":false,
                    "unsorted":true
                },
                "offset":0,
                "pageNumber":0,
                "pageSize":10,
                "paged":true,
                "unpaged":false
            },
            "last":true,
            "totalElements":1,
            "totalPages":1,
            "size":10,
            "number":0,
            "sort":{
                "empty":true,
                "sorted":false,
                "unsorted":true
            },
            "first":true,
            "numberOfElements":1,
            "empty":false
        }
        """.formatted(testUuid, testPosts.get(0).getUuid(), testPosts.get(1).getUuid());
  }

  public static String createFindUserByUuidJsonResponse(List<Post> testPosts, UUID testUuid) {
    return """
        {
            "id":"%s",
            "name":"Test User",
            "email":"test@example.com",
            "username":"testuser",
            "posts":["%s","%s"],
            "tags":["technology","java"]
        }
        """.formatted(testUuid, testPosts.get(0).getUuid(), testPosts.get(1).getUuid());
  }

  public static String createFindUserByEmailJsonResponse(List<Post> testPosts, UUID testUuid) {
    return """
        {
            "id":"%s",
            "name":"Test User",
            "email":"test@example.com",
            "username":"testuser",
            "posts":["%s","%s"],
            "tags":["technology","java"]
        }
        """.formatted(testUuid, testPosts.get(0).getUuid(), testPosts.get(1).getUuid());
  }

  public static String createFindUserByUsernameJsonResponse(List<Post> testPosts, UUID testUuid) {
    return """
        {
            "id":"%s",
            "name":"Test User",
            "email":"test@example.com",
            "username":"testuser",
            "posts":["%s","%s"],
            "tags":["technology","java"]
        }
        """.formatted(testUuid, testPosts.get(0).getUuid(), testPosts.get(1).getUuid());
  }

  public static String createSaveUserRequestJson(UUID testUuid) {
    return """
        {
            "id":"%s",
            "name":"Test User",
            "email":"test@example.com",
            "username":"testuser",
            "password":"testpassword123"
        }
        """.formatted(testUuid);
  }

  public static String createSaveUserResponseJson(UUID testUuid) {
    return """
        {
            "id":"%s",
            "name":"Test User",
            "email":"test@example.com",
            "username":"testuser"
        }
        """.formatted(testUuid);
  }

  public static String createUpdateUserRequestJson(UUID testUuid) {
    return """
        {
            "id":"%s",
            "name":"Updated User",
            "email":"updated@example.com",
            "username":"updateduser",
            "password":"updatedpassword123"
        }
        """.formatted(testUuid);
  }

  public static String createUpdateUserResponseJson(List<Post> testPosts, UUID testUuid) {
    return """
        {
            "id":"%s",
            "name":"Updated User",
            "email":"updated@example.com",
            "username":"updateduser",
            "posts":["%s","%s"],
            "tags":["technology","java"]
        }
        """.formatted(testUuid, testPosts.get(0).getUuid(), testPosts.get(1).getUuid());
  }

  public static String createUpdatePostTagsRequestJson(es.jmjg.experiments.infrastructure.controller.post.dto.UpdatePostTagsRequestDto dto) {
    try {
      return new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(dto);
    } catch (Exception e) {
      throw new RuntimeException("Failed to serialize UpdatePostTagsRequestDto", e);
    }
  }

  public static String createResetPasswordRequestJson(String resetKey, String newPassword) {
    return """
        {
            "resetKey":"%s",
            "newPassword":"%s"
        }
        """.formatted(resetKey, newPassword);
  }

  //TODO: use kebab-case for json fields
  public static String createAddPostCommentRequestJson(PostComment postComment) {
    return """
        {
            "id":"%s",
            "comment":"%s"
        }
        """.formatted(postComment.getUuid(), postComment.getComment());
  }

  public static String createAddPostCommentResponseJson(PostComment postComment) {
    return """
        {
            "id":"%s",
            "userId":"%s",
            "postId":"%s",
            "comment":"%s"
        }
        """.formatted(postComment.getUuid(), postComment.getUser().getUuid(), postComment.getPost().getUuid(), postComment.getComment());
  }

  public static String createFindPostCommentByUuidJsonResponse(PostComment postComment) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    return """
        {
            "id":"%s",
            "userId":"%s",
            "postId":"%s",
            "comment":"%s",
            "createdAt":"%s"
        }
        """.formatted(postComment.getUuid(), postComment.getPost().getUuid(), postComment.getUser().getUuid(), postComment.getComment(), postComment.getCreatedAt().format(formatter));
  }
}
