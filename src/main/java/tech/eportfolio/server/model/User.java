package tech.eportfolio.server.model;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import tech.eportfolio.server.common.constant.Role;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name = "user",
        indexes = {
                @Index(columnList = "username", name = "username_index"),
                @Index(columnList = "email", name = "email_index")
        })
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false, updatable = false)
    private Long id;
    private String firstName;
    private String lastName;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String password;
    private String title;
    private String phone;
    @Column(nullable = false, updatable = false, unique = true)
    private String username;

    private String avatarUrl;
    private boolean deleted = false;
    private boolean locked = false;
    private boolean enabled = true;

    @CreationTimestamp
    private Date createdDate;

    @UpdateTimestamp
    private Date updatedDate;

    @Column
    private String blobUUID;


    private String roles;
    private String[] authorities;

    public static User unverifiedUser() {
        User user = new User();
        user.setRoles(Role.ROLE_UNVERIFIED_USER.name());
        return user;
    }
}
