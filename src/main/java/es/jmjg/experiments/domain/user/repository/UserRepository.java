package es.jmjg.experiments.domain.user.repository;

import es.jmjg.experiments.domain.user.entity.UserUuid;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import es.jmjg.experiments.domain.user.entity.User;

public interface UserRepository {
  void deleteByUserUuid(UserUuid userUuid);

  Page<User> findAll(Pageable pageable);

  Optional<User> findByEmail(String email);

  Optional<User> findById(Integer id);

  Optional<User> findByUsername(String username);

  Optional<User> findByUserUuid(UserUuid userUuid);

  User save(User existingUser);

  List<User> findByTagId(Integer id);
}
