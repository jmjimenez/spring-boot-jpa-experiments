package es.jmjg.experiments.shared.jsonsample;

import es.jmjg.experiments.domain.post.entity.Post;
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

  public static String createResetPasswordRequestJson(String resetKey, String newPassword) {
    return """
        {
            "reset-key":"%s",
            "new-password":"%s"
        }
        """.formatted(resetKey, newPassword);
  }
}
