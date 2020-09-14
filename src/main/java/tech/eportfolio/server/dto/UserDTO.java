package tech.eportfolio.server.dto;

import lombok.Builder;
import lombok.Data;
import net.andreinc.mockneat.MockNeat;
import tech.eportfolio.server.common.constraint.ValidEmail;
import tech.eportfolio.server.common.constraint.ValidPassword;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Builder
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

    public static UserDTO mock() {
        MockNeat mock = MockNeat.threadLocal();
        String email = mock.emails().val();
        String firstName = mock.names().last().val();
        String lastName = mock.names().first().val();
        String password = mock.passwords().strong().val();
        String title = "Mr.";
        String username = mock.names().full().val();

        return UserDTO.builder().email(email).firstName(firstName).
                lastName(lastName).username(username).password(password + "aB1").title(title).build();
    }
}
