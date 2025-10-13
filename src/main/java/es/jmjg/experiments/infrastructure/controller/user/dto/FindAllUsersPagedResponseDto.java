package es.jmjg.experiments.infrastructure.controller.user.dto;

import es.jmjg.experiments.infrastructure.controller.shared.paged.PagedResponseDto;
import java.util.List;

public class FindAllUsersPagedResponseDto extends PagedResponseDto<FindAllUsersResponseDto> {
  public FindAllUsersPagedResponseDto(List<FindAllUsersResponseDto> content, int pageNumber,
    int pageSize, long totalElements, int totalPages, boolean hasNext, boolean hasPrevious) {
    super(content, pageNumber, pageSize, totalElements, totalPages, hasNext, hasPrevious);
  }
}
