package tech.eportfolio.server.model;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.util.Date;

@Data
@Document
public class UserComment {
    @Id
    private String id;

    private String username;

    private String portfolioId;

    private String comment;

    @CreatedDate
    private Date createdDate;

    private boolean deleted = false;

}
