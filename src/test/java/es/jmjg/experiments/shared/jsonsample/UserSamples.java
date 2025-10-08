package es.jmjg.experiments.shared.jsonsample;

import es.jmjg.experiments.domain.post.entity.Post;
import es.jmjg.experiments.domain.user.entity.User;
import java.util.List;
import java.util.UUID;

public final class UserSamples {

  private UserSamples() {
    // Utility class - prevent instantiation
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

  public static String createFindUserByUuidJsonResponse(User testUser) {
    String posts = buildPostsJson(testUser);
    String tags = buildTagsJson(testUser);
    String comments = buildCommentsJson(testUser);
    return """
        {
            "id":"%s",
            "name":"%s",
            "email":"%s",
            "username":"%s",
            "posts":[%s],
            "tags":[%s],
            "comments":[%s]
        }
        """.formatted(testUser.getUuid(), testUser.getName(), testUser.getEmail(), testUser.getUsername(), posts, tags, comments);
  }

  public static String createFindUserByEmailJsonResponse(User testUser) {
    String posts = buildPostsJson(testUser);
    String tags = buildTagsJson(testUser);
    String comments = buildCommentsJson(testUser);
    return """
        {
            "id":"%s",
            "name":"%s",
            "email":"%s",
            "username":"%s",
            "posts":[%s],
            "tags":[%s],
            "comments":[%s]
        }
        """.formatted(testUser.getUuid(), testUser.getName(), testUser.getEmail(), testUser.getUsername(), posts, tags, comments);
  }

  public static String createFindUserByUsernameJsonResponse(User testUser) {
    String posts = buildPostsJson(testUser);
    String tags = buildTagsJson(testUser);
    String comments = buildCommentsJson(testUser);
    return """
        {
            "id":"%s",
            "name":"%s",
            "email":"%s",
            "username":"%s",
            "posts":[%s],
            "tags":[%s],
            "comments":[%s]
        }
        """.formatted(testUser.getUuid(), testUser.getName(), testUser.getEmail(), testUser.getUsername(), posts, tags, comments);
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

  public static String createUpdateUserResponseJson(User testUser) {
    String tags = buildTagsJson(testUser);
    return """
        {
            "id":"%s",
            "name":"%s",
            "email":"%s",
            "username":"%s",
            "tags":[%s]
        }
        """.formatted(testUser.getUuid(), testUser.getName(), testUser.getEmail(), testUser.getUsername(), tags);
  }

  public static String createResetPasswordRequestJson(String resetKey, String newPassword) {
    return """
        {
            "reset-key":"%s",
            "new-password":"%s"
        }
        """.formatted(resetKey, newPassword);
  }

  public static String createFindAllUsersEmptyJsonResponse() {
    return """
      {
        "content": [],
        "pageable": {
          "sort": {
            "empty": true,
            "sorted": false,
            "unsorted": true
          },
          "offset": 0,
          "pageNumber": 0,
          "pageSize": 10,
          "paged": true,
          "unpaged": false
        },
        "last": true,
        "totalElements": 0,
        "totalPages": 0,
        "size": 10,
        "number": 0,
        "sort": {
          "empty": true,
          "sorted": false,
          "unsorted": true
        },
        "numberOfElements": 0,
        "first": true,
        "empty": true
      }""";
  }

  private static String buildPostsJson(User user) {
    return user.getPosts().stream()
        .map(post -> "\"" + post.getUuid() + "\"")
        .collect(java.util.stream.Collectors.joining(","));
  }

  private static String buildTagsJson(User user) {
    return user.getTags().stream()
        .map(tag -> "\"" + tag.getName() + "\"")
        .collect(java.util.stream.Collectors.joining(","));
  }

  private static String buildCommentsJson(User user) {
    return user.getComments().stream()
        .map(comment -> "\"" + comment.getUuid() + "\"")
        .collect(java.util.stream.Collectors.joining(","));
  }
}
