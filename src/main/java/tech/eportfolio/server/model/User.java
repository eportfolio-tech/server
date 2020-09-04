package tech.eportfolio.server.model;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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
    private boolean deleted = false;
    private boolean locked = false;
    private boolean enabled = true;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private Date createdAt;

    @Column(nullable = false)
    @UpdateTimestamp
    private Date updatedOn;

    private String roles;
    private String[] authorities;

}
