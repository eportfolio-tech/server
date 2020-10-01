package tech.eportfolio.server.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import tech.eportfolio.server.common.constant.Visibility;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class PortfolioDTO {
    @NotBlank
    private String title;

    @NotBlank
    private String description;

    @NotBlank
    private String coverImage;

    @NotBlank
    private Visibility visibility;

    public static PortfolioDTO mock() {
        String title = RandomStringUtils.randomAlphabetic(8);
        String description = RandomStringUtils.randomAlphabetic(32);
        return PortfolioDTO.builder().title(title).description(description).visibility(Visibility.PUBLIC).build();
    }

}
