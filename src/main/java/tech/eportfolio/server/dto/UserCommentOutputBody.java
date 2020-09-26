package tech.eportfolio.server.dto;

import lombok.Data;
import tech.eportfolio.server.model.UserComment;

import java.util.Date;


@Data
public class UserCommentOutputBody {
    private String id;

    private String username;

    private String portfolioId;

    private String comment;

    private Date createdDate;

    private boolean deleted;

    private String avatarUrl;

    public UserCommentOutputBody(UserComment userComment, String s) {
        this.id = userComment.getId();
        this.username = userComment.getUsername();
        this.portfolioId = userComment.getPortfolioId();
        this.comment = userComment.getComment();
        this.createdDate = userComment.getCreatedDate();
        this.deleted = userComment.isDeleted();
        this.avatarUrl = s;
    }
}
