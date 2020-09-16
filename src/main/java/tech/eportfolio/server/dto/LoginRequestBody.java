package tech.eportfolio.server.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@AllArgsConstructor
public class LoginRequestBody {
    @NotBlank
    private String username;
    @NotBlank
    private String password;
}
