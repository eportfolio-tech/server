package tech.eportfolio.server.model;

import lombok.Data;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import tech.eportfolio.server.common.constant.Visibility;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

@Data
@Entity
@Indexed
@Document
public class Portfolio {
    @Id
    private String id;

    @Field
    private String title;

    @Field
    private String username;

    private long userId;

    @Field
    private String description;

    @Field
    private String jsonUrl;

    private Visibility visibility = Visibility.PUBLIC;

    private boolean deleted = false;

    private Date createdAt = new Date();

    private Date updatedOn = new Date();
}
