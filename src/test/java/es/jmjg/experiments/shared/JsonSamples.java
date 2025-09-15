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

  // Post-related JSON methods

  /**
   * Creates JSON response for findAllPosts endpoint.
   */
  public static String createFindAllPostsJsonResponse(List<Post> posts) {
    return """
        {
            "content":[
                {
                    "uuid":"%s",
                    "userId":"%s",
                    "title":"Hello, World!",
                    "body":"This is my first post.",
                    "tags":[]
                },
                {
                    "uuid":"%s",
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

  /**
   * Creates JSON response for findAllPosts with pagination.
   */
  public static String createFindAllPostsWithPaginationJsonResponse(List<Post> posts) {
    return """
        {
            "content":[
                {
                    "uuid":"%s",
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

  /**
   * Creates JSON response for empty posts list.
   */
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

  /**
   * Creates JSON response for findPostByUuid endpoint.
   */
  public static String createFindPostByUuidJsonResponse(Post post) {
    return """
        {
            "uuid":"%s",
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

  /**
   * Creates JSON request for creating a post.
   */
  public static String createCreatePostRequestJson(Post post) {
    return """
        {
            "uuid":"%s",
            "title":"%s",
            "body":"%s"
        }
        """
        .formatted(post.getUuid(), post.getTitle(), post.getBody());
  }

  /**
   * Creates JSON response for creating a post.
   */
  public static String createCreatePostResponseJson(Post post) {
    return """
        {
            "uuid":"%s",
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

  /**
   * Creates JSON request for updating a post.
   */
  public static String createUpdatePostRequestJson(Post post) {
    return """
        {
            "uuid":"%s",
            "title":"%s",
            "body":"%s"
        }
        """
        .formatted(
            post.getUuid(),
            post.getTitle(),
            post.getBody());
  }

  /**
   * Creates JSON response for searching posts.
   */
  public static String createSearchPostsJsonResponse(List<Post> searchResults) {
    return """
        [
            {
                "uuid":"%s",
                "title":"%s",
                "body":"%s",
                "tags":[]
            },
            {
                "uuid":"%s",
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

  // User-related JSON methods

  /**
   * Creates JSON response for findAllUsers endpoint.
   */
  public static String createFindAllUsersJsonResponse(List<Post> testPosts, UUID testUuid) {
    return """
        {
            "content":[
                {
                    "uuid":"%s",
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

  /**
   * Creates JSON response for findUserByUuid endpoint.
   */
  public static String createFindUserByUuidJsonResponse(List<Post> testPosts, UUID testUuid) {
    return """
        {
            "uuid":"%s",
            "name":"Test User",
            "email":"test@example.com",
            "username":"testuser",
            "posts":["%s","%s"],
            "tags":["technology","java"]
        }
        """.formatted(testUuid, testPosts.get(0).getUuid(), testPosts.get(1).getUuid());
  }

  /**
   * Creates JSON response for findUserByEmail endpoint.
   */
  public static String createFindUserByEmailJsonResponse(List<Post> testPosts, UUID testUuid) {
    return """
        {
            "uuid":"%s",
            "name":"Test User",
            "email":"test@example.com",
            "username":"testuser",
            "posts":["%s","%s"],
            "tags":["technology","java"]
        }
        """.formatted(testUuid, testPosts.get(0).getUuid(), testPosts.get(1).getUuid());
  }

  /**
   * Creates JSON response for findUserByUsername endpoint.
   */
  public static String createFindUserByUsernameJsonResponse(List<Post> testPosts, UUID testUuid) {
    return """
        {
            "uuid":"%s",
            "name":"Test User",
            "email":"test@example.com",
            "username":"testuser",
            "posts":["%s","%s"],
            "tags":["technology","java"]
        }
        """.formatted(testUuid, testPosts.get(0).getUuid(), testPosts.get(1).getUuid());
  }

  /**
   * Creates JSON request for saving a user.
   */
  public static String createSaveUserRequestJson(UUID testUuid) {
    return """
        {
            "uuid":"%s",
            "name":"Test User",
            "email":"test@example.com",
            "username":"testuser",
            "password":"testpassword123"
        }
        """.formatted(testUuid);
  }

  /**
   * Creates JSON response for saving a user.
   */
  public static String createSaveUserResponseJson(UUID testUuid) {
    return """
        {
            "uuid":"%s",
            "name":"Test User",
            "email":"test@example.com",
            "username":"testuser"
        }
        """.formatted(testUuid);
  }

  /**
   * Creates JSON request for updating a user.
   */
  public static String createUpdateUserRequestJson(UUID testUuid) {
    return """
        {
            "uuid":"%s",
            "name":"Updated User",
            "email":"updated@example.com",
            "username":"updateduser",
            "password":"updatedpassword123"
        }
        """.formatted(testUuid);
  }

  /**
   * Creates JSON response for updating a user.
   */
  public static String createUpdateUserResponseJson(List<Post> testPosts, UUID testUuid) {
    return """
        {
            "uuid":"%s",
            "name":"Updated User",
            "email":"updated@example.com",
            "username":"updateduser",
            "posts":["%s","%s"],
            "tags":["technology","java"]
        }
        """.formatted(testUuid, testPosts.get(0).getUuid(), testPosts.get(1).getUuid());
  }

  /**
   * Serializes UpdatePostTagsRequestDto to JSON for patch tags requests.
   */
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

  //TODO: replace all references to uuid to id
  //TODO: use kebab-case for json fields
  public static String createAddPostCommentRequestJson(PostComment postComment) {
    return """
        {
            "uuid":"%s",
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
            "postId":"%s",
            "userId":"%s",
            "comment":"%s",
            "createdAt":"%s"
        }
        """.formatted(postComment.getUuid(), postComment.getPost().getUuid(), postComment.getUser().getUuid(), postComment.getComment(), postComment.getCreatedAt().format(formatter));
  }
}
