package es.jmjg.experiments.infrastructure.controller.post.dto;

import es.jmjg.experiments.infrastructure.controller.shared.paged.PagedResponseDto;
import java.util.List;

public class FindAllPostsPagedResponseDto extends PagedResponseDto<FindAllPostsResponseDto> {
  public FindAllPostsPagedResponseDto(List<FindAllPostsResponseDto> content, int pageNumber,
    int pageSize, long totalElements, int totalPages, boolean hasNext, boolean hasPrevious) {
    super(content, pageNumber, pageSize, totalElements, totalPages, hasNext, hasPrevious);
  }
}
