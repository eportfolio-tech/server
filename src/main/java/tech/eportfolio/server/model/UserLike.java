package tech.eportfolio.server.model;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.util.Date;
import java.util.LinkedList;

@Data
@Document
public class UserLike {
    @Id
    private Long id;

    private LinkedList<String> usernames;

    @TextIndexed
    private String portfolioId;

    @CreatedDate
    private Date createdDate;

}
