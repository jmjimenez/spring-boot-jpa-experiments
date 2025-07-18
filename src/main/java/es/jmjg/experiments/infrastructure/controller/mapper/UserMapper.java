package es.jmjg.experiments.infrastructure.controller.mapper;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

import es.jmjg.experiments.domain.entity.User;
import es.jmjg.experiments.infrastructure.controller.dto.UserRequestDto;
import es.jmjg.experiments.infrastructure.controller.dto.UserResponseDto;

@Component
public class UserMapper {

  public UserResponseDto toResponseDto(User user) {
    if (user == null) {
      return null;
    }
    return new UserResponseDto(
        user.getUuid(), user.getName(), user.getEmail(), user.getUsername());
  }

  public List<UserResponseDto> toResponseDtoList(List<User> users) {
    if (users == null) {
      return List.of();
    }
    return users.stream().map(this::toResponseDto).collect(Collectors.toList());
  }

  public User toDomain(UserRequestDto userRequestDto) {
    if (userRequestDto == null) {
      return null;
    }
    User user = new User();
    user.setUuid(userRequestDto.getUuid());
    user.setName(userRequestDto.getName());
    user.setEmail(userRequestDto.getEmail());
    user.setUsername(userRequestDto.getUsername());
    return user;
  }
}
