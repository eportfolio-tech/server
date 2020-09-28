package tech.eportfolio.server.model;

import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import javax.validation.constraints.NotNull;

@Data
@Document
public class UserTag {
    @Id
    private String id;

    @Indexed
    private String username;

    private String userId;

    @NotNull
    private String tagId;

    private boolean deleted = false;
}
