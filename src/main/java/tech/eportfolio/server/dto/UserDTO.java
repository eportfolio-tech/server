package tech.eportfolio.server.dto;

import lombok.Data;
import tech.eportfolio.server.constraint.EmailConstraint;

import javax.validation.constraints.NotNull;

@Data
public class UserDTO {
    @NotNull
    private String firstName;
    @NotNull
    private String lastName;
    @EmailConstraint
    private String email;
    @NotNull
    private String password;
    private String title;
    private String phone;
    private String username;
}
