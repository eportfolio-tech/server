package tech.eportfolio.server.model;

import com.mongodb.DBObject;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.util.Date;

@Data
@Document
public class Template {
    @Id
    private String id;

    @TextIndexed
    @Indexed
    private String title;

    private String description;

    private DBObject boilerplate;

    private boolean hidden = false;

    private boolean deleted = false;

    @CreatedDate
    private Date createdDate;

    @LastModifiedDate
    private Date updatedDate;
}
