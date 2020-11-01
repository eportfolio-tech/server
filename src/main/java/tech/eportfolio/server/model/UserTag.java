package tech.eportfolio.server.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@Document
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserTag implements Serializable {
    @Id
    private String id;

    @Indexed
    private String username;

    private String userId;

    @NotNull
    private String tagId;

    private boolean deleted;
}
