package tech.eportfolio.server.model;

import com.mongodb.DBObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;
import tech.eportfolio.server.common.constant.Visibility;

import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

@Data
@Document
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Portfolio implements Serializable {

    private static final long serialVersionUID = 1234567L;

    @Id
    private String id;

    @TextIndexed
    private String title;

    private String coverImage;

    private String music;

    @Indexed(unique = true)
    @TextIndexed
    private String username;

    private String userId;

    @TextIndexed
    private String description;

    @TextIndexed
    private DBObject content;

    private Visibility visibility = Visibility.PUBLIC;

    private boolean deleted = false;

    @CreatedDate
    private Date createdDate;

    @LastModifiedDate
    private Date updatedDate;

}
