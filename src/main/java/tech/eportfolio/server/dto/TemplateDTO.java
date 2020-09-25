package tech.eportfolio.server.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.javafaker.Faker;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.RandomStringUtils;

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

    public static TemplateDTO mock() {
        String title = Faker.instance().name().title();
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode();
        JsonNode boilerplate = objectNode.put(RandomStringUtils.randomAlphabetic(4), RandomStringUtils.randomAlphanumeric(8));
        String description = Faker.instance().gameOfThrones().quote();

        return TemplateDTO.builder().title(title).boilerplate(boilerplate).description(description).build();
    }

}
