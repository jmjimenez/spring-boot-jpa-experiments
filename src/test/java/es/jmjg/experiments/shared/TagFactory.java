package es.jmjg.experiments.shared;

import java.util.UUID;

import es.jmjg.experiments.domain.entity.Tag;

/**
 * Factory class for creating test Tag instances with various configurations.
 * Provides methods to
 * create tags with different names and UUIDs.
 */
public class TagFactory {

  /**
   * Creates a basic tag with default values.
   *
   * @return a new Tag instance with default values
   */
  public static Tag createBasicTag() {
    Tag tag = new Tag();
    tag.setUuid(UUID.randomUUID());
    tag.setName("test-tag");
    return tag;
  }

  /**
   * Creates a tag with custom name.
   *
   * @param name the name for the tag
   * @return a new Tag instance with the specified name
   */
  public static Tag createTag(String name) {
    Tag tag = new Tag();
    tag.setUuid(UUID.randomUUID());
    tag.setName(name);
    return tag;
  }

  /**
   * Creates a tag with a specific UUID and name.
   *
   * @param uuid the UUID for the tag
   * @param name the name for the tag
   * @return a new Tag instance with the specified UUID and name
   */
  public static Tag createTag(UUID uuid, String name) {
    Tag tag = new Tag();
    tag.setUuid(uuid);
    tag.setName(name);
    return tag;
  }

  /**
   * Creates a technology tag.
   *
   * @return a new Tag instance for technology
   */
  public static Tag createTechnologyTag() {
    return createTag("technology");
  }

  /**
   * Creates a programming tag.
   *
   * @return a new Tag instance for programming
   */
  public static Tag createProgrammingTag() {
    return createTag("programming");
  }

  /**
   * Creates a Java tag.
   *
   * @return a new Tag instance for Java
   */
  public static Tag createJavaTag() {
    return createTag("java");
  }

  /**
   * Creates a Spring Boot tag.
   *
   * @return a new Tag instance for Spring Boot
   */
  public static Tag createSpringBootTag() {
    return createTag("spring-boot");
  }

  /**
   * Creates a JPA tag.
   *
   * @return a new Tag instance for JPA
   */
  public static Tag createJpaTag() {
    return createTag("jpa");
  }

  /**
   * Creates a database tag.
   *
   * @return a new Tag instance for database
   */
  public static Tag createDatabaseTag() {
    return createTag("database");
  }

  /**
   * Creates a web development tag.
   *
   * @return a new Tag instance for web development
   */
  public static Tag createWebDevelopmentTag() {
    return createTag("web-development");
  }

  /**
   * Creates a tutorial tag.
   *
   * @return a new Tag instance for tutorial
   */
  public static Tag createTutorialTag() {
    return createTag("tutorial");
  }

  /**
   * Creates a best practices tag.
   *
   * @return a new Tag instance for best practices
   */
  public static Tag createBestPracticesTag() {
    return createTag("best-practices");
  }

  /**
   * Creates an architecture tag.
   *
   * @return a new Tag instance for architecture
   */
  public static Tag createArchitectureTag() {
    return createTag("architecture");
  }

  /**
   * Creates a microservices tag.
   *
   * @return a new Tag instance for microservices
   */
  public static Tag createMicroservicesTag() {
    return createTag("microservices");
  }

  /**
   * Creates a testing tag.
   *
   * @return a new Tag instance for testing
   */
  public static Tag createTestingTag() {
    return createTag("testing");
  }

  /**
   * Creates a DevOps tag.
   *
   * @return a new Tag instance for DevOps
   */
  public static Tag createDevOpsTag() {
    return createTag("devops");
  }

  /**
   * Creates an API tag.
   *
   * @return a new Tag instance for API
   */
  public static Tag createApiTag() {
    return createTag("api");
  }

  /**
   * Creates a security tag.
   *
   * @return a new Tag instance for security
   */
  public static Tag createSecurityTag() {
    return createTag("security");
  }
}