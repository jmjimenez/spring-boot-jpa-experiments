package es.jmjg.experiments.domain.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "Users")
@NoArgsConstructor
@Getter
@Setter
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @NotNull
  @Column(name = "uuid", unique = true, nullable = false)
  private UUID uuid;

  @NotEmpty
  private String name;

  @NotEmpty
  @Column(unique = true)
  private String email;

  @Column(unique = true)
  private String username;

  @OneToMany(mappedBy = "user", cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
  private List<Post> posts = new ArrayList<>();

  // Constructor with UUID
  public User(Integer id, UUID uuid, String name, String email, String username, List<Post> posts) {
    this.id = id;
    this.uuid = uuid;
    this.name = name;
    this.email = email;
    this.username = username;
    this.posts = posts != null ? posts : new ArrayList<>();
  }
}
