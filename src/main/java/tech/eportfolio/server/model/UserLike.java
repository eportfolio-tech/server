package tech.eportfolio.server.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;

@Data
@Document
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserLike implements Serializable {
    @Id
    private String id;

    private String userId;

    @Indexed
    private String username;

    @Indexed
    private String portfolioId;

    @CreatedDate
    private Date createdDate;

    private boolean deleted;

}
