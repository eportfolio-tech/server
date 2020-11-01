package tech.eportfolio.server.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

@Data
@Document
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FeedHistory implements Serializable {
    @Id
    private String id;

    @Indexed
    private String userId;

    private Set<String> feedItems;

    private boolean deleted;

    @CreatedDate
    private Date createdDate;

    @LastModifiedDate
    private Date updatedDate;
}
