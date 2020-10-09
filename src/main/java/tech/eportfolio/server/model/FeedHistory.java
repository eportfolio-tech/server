package tech.eportfolio.server.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.util.Date;
import java.util.Set;

@Data
@Document
@Builder
public class FeedHistory {
    @Id
    private String id;

    private String userId;

    private Set<String> feedItems;

    private boolean deleted;

    @CreatedDate
    private Date createdDate;

    @LastModifiedDate
    private Date updatedDate;
}
