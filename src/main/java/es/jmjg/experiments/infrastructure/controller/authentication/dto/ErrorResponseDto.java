package es.jmjg.experiments.infrastructure.controller.authentication.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Error response DTO")
public class ErrorResponseDto {
    @Schema(description = "Error message", example = "Invalid credentials")
    private String error;

    @Schema(description = "HTTP status code", example = "401")
    private int status;
}
