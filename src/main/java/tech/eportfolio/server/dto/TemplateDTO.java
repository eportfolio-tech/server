package tech.eportfolio.server.dto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@AllArgsConstructor
public class TemplateDTO {
    @NotBlank
    private String title;

    @NotBlank
    private JsonNode boilerplate;

    @NotBlank
    private String description;
}
