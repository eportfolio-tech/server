package tech.eportfolio.server.model;

import lombok.Data;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;
import tech.eportfolio.server.common.constant.Visibility;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

@Data
@Entity
@Document
public class Portfolio {
    @Id
    private String id;

    @TextIndexed
    private String title;

    @TextIndexed
    private String username;

    private long userId;

    @TextIndexed
    private String description;

    private String jsonUrl;

    private Visibility visibility = Visibility.PUBLIC;

    private boolean deleted = false;

    private Date createdAt = new Date();

    private Date updatedOn = new Date();

}
