package tech.eportfolio.server.model;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.util.Date;

@Data
@Document
public class UserComment {
    @Id
    private String id;

    @Indexed
    private String username;

    @Indexed
    private String portfolioId;

    private String comment;

    @CreatedDate
    private Date createdDate;

    private boolean deleted = false;

}
