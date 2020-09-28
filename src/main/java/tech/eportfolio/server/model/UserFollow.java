package tech.eportfolio.server.model;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.util.Date;

@Data
@Document
public class UserFollow {
    @Id
    private String id;

    // source user follows destination user
    @Indexed
    private String sourceUsername;

    @Indexed
    private String destinationUsername;

    @CreatedDate
    private Date createdDate;

    private boolean deleted = false;

}
