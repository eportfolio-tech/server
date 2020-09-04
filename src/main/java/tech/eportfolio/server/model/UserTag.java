package tech.eportfolio.server.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Data
@Entity
@Table(name = "user_tag",
        indexes = {
                @Index(columnList = "username", name = "username_index"),
                @Index(columnList = "tagId", name = "tagId_index")
        })
public class UserTag {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    private String username;

    @NotNull
    private Long userID;

    @NotNull
    private Long tagId;
}
