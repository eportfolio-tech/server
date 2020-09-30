package tech.eportfolio.server.dto;

import lombok.Data;
import tech.eportfolio.server.model.UserComment;

import java.util.Date;


@Data
public class UserCommentOutputBody {
    private String id;

    private String username;

    private String portfolioId;

    private String content;

    private Date createdDate;

    private boolean deleted;

    private String avatarUrl;

    private String parentId;

    public UserCommentOutputBody(UserComment userComment, String avatarUrl) {
        this.id = userComment.getId();
        this.username = userComment.getUsername();
        this.portfolioId = userComment.getPortfolioId();
        this.content = userComment.getContent();
        this.createdDate = userComment.getCreatedDate();
        this.deleted = userComment.isDeleted();
        this.avatarUrl = avatarUrl;
        this.parentId = userComment.getParentId();
    }
}
