package tech.eportfolio.server.model;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import tech.eportfolio.server.constant.Visibility;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "portfolio",
        indexes = {
                @Index(columnList = "userId", name = "user_id_index"),
        })
public class Portfolio {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, updatable = false)
    private Long userId;

    private String content;

    private Visibility visibility = Visibility.PUBLIC;

    private boolean deleted = false;

    @Column(nullable = false, updatable = false)
    private String createdBy;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private Date createdAt;

    @Column(nullable = false)
    @UpdateTimestamp
    private Date updatedOn;
}
