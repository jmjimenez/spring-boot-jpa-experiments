package es.jmjg.experiments.domain.user.entity;

import es.jmjg.experiments.domain.post.entity.Post;
import es.jmjg.experiments.domain.post.entity.PostComment;
import es.jmjg.experiments.domain.tag.entity.Tag;
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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
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

  @NotEmpty
  @Column(nullable = false)
  private String password;

  @OneToMany(mappedBy = "user", cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
  private List<Post> posts = new ArrayList<>();

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(name = "user_tag", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "tag_id"))
  private List<Tag> tags = new ArrayList<>();

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  private List<PostComment> comments = new ArrayList<>();
}
