package tech.eportfolio.server.model;

import com.mongodb.DBObject;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import tech.eportfolio.server.common.constant.ActivityType;
import tech.eportfolio.server.common.constant.ParentType;

import javax.persistence.Id;
import java.util.Date;

@Data
@Document

public class Activity {
    @Id
    private String id;

    // type of activity (tag, portfolio, update)
    private ActivityType activityType;

    // user who generated activity
    private String userId;

    // the parent activity type (tag, portfolio)
    private ParentType parentType;

    // primary key of related portfolio or tag
    private String parentId;

    // serialized object with meta-data
    private DBObject data;

    private boolean deleted = false;

    @CreatedDate
    private Date createdDate;

    @LastModifiedDate
    private Date updatedDate;
}
