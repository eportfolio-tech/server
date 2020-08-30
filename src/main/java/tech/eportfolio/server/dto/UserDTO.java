package tech.eportfolio.server.dto;

import lombok.Data;
import tech.eportfolio.server.constraint.EmailConstraint;
import tech.eportfolio.server.constraint.FieldsConstraint;


@FieldsConstraint.List({
        @FieldsConstraint(
                field = "password",
                fieldMatch = "password_verification",
                message = "Passwords do not match!"
        ),
        @FieldsConstraint(
                field = "email",
                fieldMatch = "email_verification",
                message = "Email addresses do not match!"
        )
})
@Data
public class UserDTO {
    private String firstName;
    private String lastName;
    @EmailConstraint
    private String email;
    private String email_verification;
    private String password;
    private String password_verification;
    private String title;
    private String phone;
    private String username;
}
