package tech.eportfolio.server.model;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import tech.eportfolio.server.constant.Visibility;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Indexed
@Table(name = "portfolio",
        indexes = {
                @Index(columnList = "userId", name = "user_id_index"),
        })
public class Portfolio {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Field
    @Column(nullable = false)
    private String title;

    @Column(nullable = false, updatable = false, unique = true)
    private Long userId;

    @Field
    @Column(nullable = false, updatable = false, unique = true)
    private String createdBy;

    @Field
    private String content;

    @Field
    private String description;

    private Visibility visibility = Visibility.PUBLIC;

    private boolean deleted = false;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private Date createdAt;

    @Column(nullable = false)
    @UpdateTimestamp
    private Date updatedOn;
}
