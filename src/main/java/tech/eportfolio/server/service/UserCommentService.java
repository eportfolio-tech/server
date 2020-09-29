package tech.eportfolio.server.service;

import tech.eportfolio.server.model.Portfolio;
import tech.eportfolio.server.model.User;
import tech.eportfolio.server.model.UserComment;

import java.util.List;
import java.util.Optional;

public interface UserCommentService {

    UserComment create(User user, Portfolio portfolio, String comment);

    UserComment delete(UserComment userComment);

    Optional<UserComment> findByUsernameAndIdAndDeleted(String username, String id, boolean deleted);

    List<UserComment> findByPortfolio(Portfolio portfolio);

    List<User> findUsersByUserComments(List<UserComment> userComments);

    UserComment reply(User user, UserComment inReplyTo, String content);

    Optional<UserComment> findById(String id);
}
