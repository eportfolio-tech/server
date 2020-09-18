package tech.eportfolio.server.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.RandomStringUtils;
import tech.eportfolio.server.common.constant.Visibility;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
public class PortfolioDTO {
    @NotNull
    private String title;

    @NotNull
    @NotEmpty
    private String description;

    @NotNull
    private Visibility visibility;

    public static PortfolioDTO mock() {
        String title = RandomStringUtils.randomAlphabetic(8);
        String description = RandomStringUtils.randomAlphabetic(32);
        return PortfolioDTO.builder().title(title).description(description).build();
    }

}
