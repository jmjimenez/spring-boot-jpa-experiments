package es.jmjg.experiments.infrastructure.controller.post.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Generic paginated response wrapper")
public class PagedResponseDto<T> {
  @Schema(description = "List of items in the current page")
  private List<T> content;

  @Schema(description = "Current page number (0-based)", example = "0")
  private int pageNumber;

  @Schema(description = "Number of items per page", example = "20")
  private int pageSize;

  @Schema(description = "Total number of items across all pages", example = "100")
  private long totalElements;

  @Schema(description = "Total number of pages", example = "5")
  private int totalPages;

  @Schema(description = "Whether there is a next page", example = "true")
  private boolean hasNext;

  @Schema(description = "Whether there is a previous page", example = "false")
  private boolean hasPrevious;
}