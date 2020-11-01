package tech.eportfolio.server.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;

@Data
@Document
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Tag implements Serializable {
    @Id
    private String id;

    @Indexed(unique = true)
    private String name;

    private String icon;

    private boolean deleted;

    private String createdBy;

    @CreatedDate
    private Date createdDate;

    @LastModifiedDate
    private Date updatedDate;
}
