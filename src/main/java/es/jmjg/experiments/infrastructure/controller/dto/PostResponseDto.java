package es.jmjg.experiments.infrastructure.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostResponseDto {
    private Integer id;
    private Integer userId;
    private String title;
    private String body;
}
