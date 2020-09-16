package tech.eportfolio.server.dto;

import lombok.Builder;
import lombok.Data;
import net.andreinc.mockneat.MockNeat;
import tech.eportfolio.server.common.constraint.ValidEmail;

import javax.validation.constraints.Size;

@Data
@Builder
public class UserPatchRequestBody {
    @Size(min = 1)
    private String firstName;
    @Size(min = 1)
    private String lastName;
    @ValidEmail
    private String email;
    @Size(min = 1)
    private String title;
    @Size(min = 1)
    private String phone;
    @Size(min = 10)
    private String avatarUrl;

    public static UserPatchRequestBody mock() {
        MockNeat mock = MockNeat.threadLocal();
        String email = mock.emails().val();
        String firstName = mock.names().last().val();
        String lastName = mock.names().first().val();
        String title = "Mr.";
        String url = mock.urls().get();

        return UserPatchRequestBody.builder().email(email).firstName(firstName).
                lastName(lastName).title(title).avatarUrl(url).build();
    }
}
