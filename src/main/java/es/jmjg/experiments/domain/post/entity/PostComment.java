package es.jmjg.experiments.domain.post.entity;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.UUID;

import es.jmjg.experiments.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "post_comment")
@NoArgsConstructor
@Getter
@Setter
public class PostComment {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "uuid", nullable = false, unique = true)
  private UUID uuid;

  @NotBlank
  @Column(name = "comment", columnDefinition = "TEXT", nullable = false)
  private String comment;

  @Column(name = "created_at", insertable = false, updatable = false)
  @CreationTimestamp
  private LocalDateTime createdAt;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "post_id", nullable = false)
  private Post post;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;
}
