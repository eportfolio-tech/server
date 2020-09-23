package tech.eportfolio.server.dto;

import com.github.javafaker.Faker;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.RandomStringUtils;
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
        String email = Faker.instance().internet().emailAddress();
        String firstName = Faker.instance().name().firstName();
        String lastName = Faker.instance().name().lastName();
        String password = Faker.instance().internet().password() + RandomStringUtils.randomAlphanumeric(5).toUpperCase();
        String title = Faker.instance().name().title();
        String username = Faker.instance().name().username();

        return UserDTO.builder().email(email).firstName(firstName).
                lastName(lastName).username(username).password(password).title(title).build();
    }
}
