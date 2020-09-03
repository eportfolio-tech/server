package tech.eportfolio.server.dto;

import lombok.Data;
import tech.eportfolio.server.constraint.ValidEmail;
import tech.eportfolio.server.constraint.ValidPassword;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class UserDTO {
    @NotNull
    @NotEmpty
    private String firstName;
    @NotNull
    private String lastName;
    @ValidEmail
    private String email;
    @NotNull
    @ValidPassword
    private String password;
    @NotNull
    @NotEmpty
    private String title;
    private String phone;
    private String username;
}
