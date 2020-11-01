package tech.eportfolio.server.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import tech.eportfolio.server.common.constant.Role;

import java.io.Serializable;
import java.util.Date;

@Data
@Document
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User implements Serializable {

    private static final long serialVersionUID = 12345678L;

    @Id
    private String id;

    private String firstName;
    private String lastName;

    @Indexed(name = "email_index")
    private String email;

    private String password;
    private String organisation;
    private String title;
    private String phone;

    @Indexed(unique = true)
    private String username;

    private String avatarUrl;
    private boolean deleted;
    private boolean locked;
    private boolean enabled;

    @CreatedDate
    private Date createdDate;

    @LastModifiedDate
    private Date updatedDate;

    private String blobUUID;


    private String roles;
    private String[] authorities;

    public static User unverifiedUser() {
        User user = new User();
        user.setRoles(Role.ROLE_UNVERIFIED_USER.name());
        return user;
    }
}
