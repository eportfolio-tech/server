package tech.eportfolio.server.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import tech.eportfolio.server.common.constant.FeedType;
import tech.eportfolio.server.common.constant.ParentType;

import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

@Data
@Document
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Activity implements Serializable {

    @Id
    private String id;

    // type of feed (tag, portfolio, update)
    private FeedType feedType;

    // user who generated activity
    private String username;

    // the parent activity type (tag, portfolio)
    private ParentType parentType;

    // primary key of related portfolio or tag
    private String parentId;

    private boolean deleted;

    @CreatedDate
    private Date createdDate;
}
