package tech.eportfolio.server.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class PasswordResetRequestBody {
    @NotNull
    @NotEmpty
    private String oldPassword;
    @NotNull
    @NotEmpty
    private String newPassword;
}
