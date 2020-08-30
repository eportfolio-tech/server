package tech.eportfolio.server.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class User {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    private String firstName;
    private String lastName;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;
    private String title;
    private String phone;

    @Column(nullable = false, unique = true)
    private String username;
    private boolean deleted = false;
    private boolean locked = false;
    private boolean enabled = true;

}
