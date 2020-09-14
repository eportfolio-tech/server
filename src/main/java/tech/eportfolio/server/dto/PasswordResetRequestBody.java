package tech.eportfolio.server.dto;

import lombok.Builder;
import lombok.Data;
import tech.eportfolio.server.common.constraint.ValidPassword;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class PasswordResetRequestBody {
    @NotNull
    @NotEmpty
    private String oldPassword;
    @NotNull
    @NotEmpty
    @ValidPassword
    private String newPassword;
}
