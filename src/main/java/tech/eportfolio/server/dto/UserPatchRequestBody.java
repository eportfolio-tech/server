package tech.eportfolio.server.dto;

import lombok.Builder;
import lombok.Data;
import net.andreinc.mockneat.MockNeat;
import tech.eportfolio.server.common.constraint.ValidEmail;

@Data
@Builder
public class UserPatchRequestBody {
    private String firstName;

    private String lastName;
    @ValidEmail
    private String email;

    private String title;
    private String phone;

    public static UserPatchRequestBody mock() {
        MockNeat mock = MockNeat.threadLocal();
        String email = mock.emails().val();
        String firstName = mock.names().last().val();
        String lastName = mock.names().first().val();
        String title = "Mr.";

        return UserPatchRequestBody.builder().email(email).firstName(firstName).
                lastName(lastName).title(title).build();
    }
}
