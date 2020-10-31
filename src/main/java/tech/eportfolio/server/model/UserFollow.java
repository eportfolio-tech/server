package tech.eportfolio.server.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

@Data
@Document
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserFollow implements Serializable {
    @Id
    private String id;

    private String sourceUserId;

    // source user follows destination user
    @Indexed
    private String sourceUsername;

    @Indexed
    private String destinationUsername;

    @CreatedDate
    private Date createdDate;

    private boolean deleted = false;

}
