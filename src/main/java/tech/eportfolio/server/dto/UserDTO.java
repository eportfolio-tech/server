package tech.eportfolio.server.dto;

import lombok.Data;
import tech.eportfolio.server.constraint.EmailConstraint;

@Data
public class UserDTO {
    private String firstName;
    private String lastName;
    @EmailConstraint
    private String email;
    private String password;
    private String title;
    private String phone;
    private String username;
}
