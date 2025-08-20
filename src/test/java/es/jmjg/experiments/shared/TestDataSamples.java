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
  public static final UUID TECHNOLOGY_UUID = UUID.fromString("550e8400-e29b-41d4-a716-446655440056");
  public static final UUID JAVA_UUID = UUID.fromString("550e8400-e29b-41d4-a716-446655440058");
  public static final UUID DEVELOPER_UUID = UUID.fromString("550e8400-e29b-41d4-a716-446655440071");
  public static final UUID NOT_USED_UUID = UUID.fromString("550e8400-e29b-41d4-a716-446655440072");

  // Sample users from Flyway migration data
  public static final UUID LEANNE_UUID = UUID.fromString("550e8400-e29b-41d4-a716-446655440001");
  public static final String LEANNE_NAME = "Leanne Graham";
  public static final String LEANNE_EMAIL = "leanne.graham@example.com";
  public static final String LEANNE_USERNAME = "leanne_graham";

  public static final String ERVIN_NAME = "Ervin Howell";
  public static final String ERVIN_EMAIL = "ervin.howell@example.com";
  public static final String ERVIN_USERNAME = "ervin_howell";

  public static final String CLEMENTINE_NAME = "Clementine Bauch";
  public static final String CLEMENTINE_EMAIL = "clementine.bauch@example.com";
  public static final String CLEMENTINE_USERNAME = "clementine_bauch";

  public static final String ADMIN_USERNAME = "admin";
  public static final String ADMIN_PASSWORD = "testpass";

  public static final UUID PATRICIA_UUID = UUID.fromString("550e8400-e29b-41d4-a716-446655440004");

  public static final UUID CHELSEY_UUID = UUID.fromString("550e8400-e29b-41d4-a716-446655440005");

  // Sample posts from Flyway migration data
  public static final UUID POST_1_UUID = UUID.fromString("550e8400-e29b-41d4-a716-446655440006");
  public static final String POST_1_TITLE = "sunt aut facere repellat provident occaecati excepturi optio reprehenderit";
  public static final String POST_1_BODY = "quia et suscipit\\nsuscipit recusandae consequuntur expedita et cum\\nreprehenderit molestiae ut ut quas totam\\nnostrum rerum est autem sunt rem eveniet architecto";
  public static final UUID POST_2_UUID = UUID.fromString("550e8400-e29b-41d4-a716-446655440007");
  public static final String POST_2_TITLE = "qui est esse";
  public static final UUID POST_3_UUID = UUID.fromString("550e8400-e29b-41d4-a716-446655440008");
  public static final String POST_3_TITLE = "ea molestias quasi exercitationem repellat qui ipsa sit aut";

  // Additional post titles and bodies from Flyway migration data
  public static final String ERVIN_POST_TITLE = "et ea vero quia laudantium autem";
  public static final String ERVIN_POST_BODY = "delectus reiciendis molestiae occaecati non minima eveniet qui voluptatibus\\naccusamus in eum beatae sit\\nvel qui neque voluptates ut commodi qui incidunt\\nut animi commodi";
  public static final String CLEMENTINE_POST_TITLE = "asperiores ea ipsam voluptatibus modi minima quia sint";
  public static final String CLEMENTINE_POST_BODY = "repellat aliquid praesentium dolorem quo\\nsed totam minus non itaque\\nnihil labore molestiae sunt dolor eveniet hic recusandae veniam\\ntempora et tenetur expedita sunt";

  // Sample tag names from Flyway migration data
  public static final String TECHNOLOGY_TAG_NAME = "technology";
  public static final String JAVA_TAG_NAME = "java";
  public static final String SPRING_BOOT_TAG_NAME = "spring-boot";
  public static final String JPA_TAG_NAME = "jpa";
  public static final String DATABASE_TAG_NAME = "database";

  // Additional tag UUIDs from Flyway migration data
  public static final UUID SPRING_BOOT_TAG_UUID = UUID.fromString("550e8400-e29b-41d4-a716-446655440059");
  public static final UUID DATABASE_TAG_UUID = UUID.fromString("550e8400-e29b-41d4-a716-446655440061");

  // Sample search terms and expected counts from Flyway migration data
  public static final String SEARCH_TERM_SUNT = "fugiat";
  public static final int EXPECTED_SUNT_SEARCH_COUNT = 3;
}
