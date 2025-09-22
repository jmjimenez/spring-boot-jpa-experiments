package es.jmjg.experiments.shared;

import java.util.UUID;

/**
 * Test data samples from Flyway migration data.
 * Contains constants used across integration tests.
 */
public final class TestDataSamples {

  private TestDataSamples() {
    // Utility class - prevent instantiation
  }

  // Sample tags from Flyway migration data
  public static final UUID TAG_TECHNOLOGY_UUID = UUID.fromString("550e8400-e29b-41d4-a716-446655440056");
  public static final UUID TAG_JAVA_UUID = UUID.fromString("550e8400-e29b-41d4-a716-446655440058");
  public static final UUID TAG_DEVELOPER_UUID = UUID.fromString("550e8400-e29b-41d4-a716-446655440071");
  public static final UUID TAG_SPRING_BOOT_UUID = UUID.fromString("550e8400-e29b-41d4-a716-446655440059");
  public static final UUID TAG_NOT_USED_UUID = UUID.fromString("550e8400-e29b-41d4-a716-446655440072");
  public static final UUID TAG_PROGRAMMING_UUID = UUID.fromString("550e8400-e29b-41d4-a716-446655440057");
  public static final UUID TAG_JPA_UUID = UUID.fromString("550e8400-e29b-41d4-a716-446655440060");
  public static final UUID TAG_DATABASE_UUID = UUID.fromString("550e8400-e29b-41d4-a716-446655440061");
  public static final UUID TAG_WEB_DEVELOPMENT_UUID = UUID.fromString("550e8400-e29b-41d4-a716-446655440062");
  public static final UUID TAG_BEST_PRACTICES_UUID = UUID.fromString("550e8400-e29b-41d4-a716-446655440064");

  // Tag name constants for tests
  public static final String TAG_JAVA = "java";
  public static final String TAG_SPRING_BOOT = "spring-boot";
  public static final String TAG_DEVELOPER = "developer";
  public static final String TAG_PROGRAMMING = "programming";
  public static final String TAG_WEB_DEVELOPMENT = "web-development";
  public static final String TAG_BEST_PRACTICES = "best-practices";
  public static final String TAG_TECHNOLOGY = "technology";
  public static final String TAG_JPA = "jpa";
  public static final String TAG_DATABASE = "database";

  // Sample users from Flyway migration data
  public static final int LEANNE_ID = 1;
  public static final UUID LEANNE_UUID = UUID.fromString("550e8400-e29b-41d4-a716-446655440001");
  public static final String LEANNE_NAME = "Leanne Graham";
  public static final String LEANNE_EMAIL = "leanne.graham@example.com";
  public static final String LEANNE_USERNAME = "leanne_graham";
  public static final String LEANNE_PASSWORD = "testpass";

  public static final int ERVIN_ID = 2;
  public static final UUID ERVIN_UUID = UUID.fromString("550e8400-e29b-41d4-a716-446655440002");
  public static final String ERVIN_NAME = "Ervin Howell";
  public static final String ERVIN_EMAIL = "ervin.howell@example.com";
  public static final String ERVIN_USERNAME = "ervin_howell";

  public static final int CLEMENTINE_ID = 3;
  public static final UUID CLEMENTINE_UUID = UUID.fromString("550e8400-e29b-41d4-a716-446655440003");
  public static final String CLEMENTINE_NAME = "Clementine Bauch";
  public static final String CLEMENTINE_EMAIL = "clementine.bauch@example.com";
  public static final String CLEMENTINE_USERNAME = "clementine_bauch";

  public static final UUID ADMIN_UUID = UUID.fromString("550e8400-e29b-41d4-a716-446655440006");
  public static final String ADMIN_NAME = "Admin User";
  public static final String ADMIN_USERNAME = "admin";
  public static final String ADMIN_PASSWORD = "testpass";
  public static final String ADMIN_EMAIL = "admin@example.com";
  public static final String USER_PASSWORD = "testpass";

  public static final UUID PATRICIA_UUID = UUID.fromString("550e8400-e29b-41d4-a716-446655440004");
  public static final String PATRICIA_NAME = "Patricia Lebsack";
  public static final String PATRICIA_EMAIL = "patricia.lebsack@example.com";
  public static final String PATRICIA_USERNAME = "patricia_lebsack";

  public static final UUID CHELSEY_UUID = UUID.fromString("550e8400-e29b-41d4-a716-446655440005");
  public static final String CHELSEY_NAME = "Chelsey Dietrich";
  public static final String CHELSEY_EMAIL = "chelsey.dietrich@example.com";
  public static final String CHELSEY_USERNAME = "chelsey_dietrich";

  // Sample posts from Flyway migration data
  public static final UUID POST_1_UUID = UUID.fromString("550e8400-e29b-41d4-a716-446655440006");
  public static final String POST_1_TITLE = "sunt aut facere repellat provident occaecati excepturi optio reprehenderit";
  public static final String POST_1_BODY = "quia et suscipit\\nsuscipit recusandae consequuntur expedita et cum\\nreprehenderit molestiae ut ut quas totam\\nnostrum rerum est autem sunt rem eveniet architecto";
  public static final UUID POST_2_UUID = UUID.fromString("550e8400-e29b-41d4-a716-446655440007");
  public static final String POST_2_TITLE = "qui est esse";
  public static final UUID POST_3_UUID = UUID.fromString("550e8400-e29b-41d4-a716-446655440008");
  public static final UUID POST_16_UUID = UUID.fromString("550e8400-e29b-41d4-a716-446655440016");

  // Additional post titles and bodies from Flyway migration data
  public static final String LEANNE_POST_TITLE = "sunt aut facere repellat provident occaecati excepturi optio reprehenderit";
  public static final UUID LEANNE_POST_UUID = UUID.fromString("550e8400-e29b-41d4-a716-446655440006");
  public static final String ERVIN_POST_TITLE = "et ea vero quia laudantium autem";
  public static final String ERVIN_POST_BODY = "delectus reiciendis molestiae occaecati non minima eveniet qui voluptatibus\\naccusamus in eum beatae sit\\nvel qui neque voluptates ut commodi qui incidunt\\nut animi commodi";
  public static final UUID ERVIN_POST_UUID = UUID.fromString("550e8400-e29b-41d4-a716-446655440016");
  public static final String CLEMENTINE_POST_TITLE = "asperiores ea ipsam voluptatibus modi minima quia sint";
  public static final String CLEMENTINE_POST_BODY = "repellat aliquid praesentium dolorem quo\\nsed totam minus non itaque\\nnihil labore molestiae sunt dolor eveniet hic recusandae veniam\\ntempora et tenetur expedita sunt";
  public static final UUID CLEMENTINE_POST_UUID = UUID.fromString("550e8400-e29b-41d4-a716-446655440026");


  // Sample search terms and expected counts from Flyway migration data
  public static final String SEARCH_TERM_SUNT = "fugiat";
  public static final int EXPECTED_SUNT_SEARCH_COUNT = 3;

  // Test post data for new posts (not from migration data)
  public static final String NEW_POST_TITLE = "Test Post 1";
  public static final String NEW_POST_BODY = "Test Body 1";

  public static final UUID COMMENT_LEANNE_POST_BY_ERWIN_UUID = UUID.fromString("550e8400-e29b-41d4-a716-446655442001");
  public static final UUID COMMENT_LEANNE_POST_BY_CLEMENTINE_UUID = UUID.fromString("550e8400-e29b-41d4-a716-446655442002");
}
