package es.jmjg.experiments.domain.tag.entity;

import es.jmjg.experiments.domain.post.entity.Post;
import es.jmjg.experiments.domain.user.entity.User;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tag")
@NoArgsConstructor
@Getter
@Setter
public class Tag {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @NotNull
  @Column(name = "uuid", unique = true, nullable = false)
  private UUID uuid;

  @NotEmpty
  @Column(name = "tag", unique = true, nullable = false)
  private String name;

  @ManyToMany(mappedBy = "tags", fetch = FetchType.LAZY)
  private List<User> users = new ArrayList<>();

  @ManyToMany(mappedBy = "tags", fetch = FetchType.LAZY)
  private List<Post> posts = new ArrayList<>();

  // Constructor with UUID
  public Tag(Integer id, UUID uuid, String name) {
    this.id = id;
    this.uuid = uuid;
    this.name = name;
  }
}
