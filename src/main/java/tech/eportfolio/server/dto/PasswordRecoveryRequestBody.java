package tech.eportfolio.server.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class PasswordRecoveryRequestBody {
    @NotNull
    @NotEmpty
    private String token;
    @NotNull
    @NotEmpty
    private String newPassword;
}
