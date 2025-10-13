package es.jmjg.experiments.infrastructure.controller.shared.paged;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;

public class PagedResponseMapper {

  public static <E, C> PagedResponseDto<C> createPagedResponse(Page<E> page, Function<E, C> converter) {
    if (page == null) {
      return new PagedResponseDto<>(List.of(), 0, 0, 0, 0, false, false);
    }

    List<C> content = page.getContent().stream()
      .map(converter)
      .collect(Collectors.toList());

    return new PagedResponseDto<>(
      content,
      page.getNumber(),
      page.getSize(),
      page.getTotalElements(),
      page.getTotalPages(),
      page.hasNext(),
      page.hasPrevious());
  }
}
