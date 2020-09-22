package tech.eportfolio.server.service;

import tech.eportfolio.server.model.Portfolio;
import tech.eportfolio.server.model.User;
import tech.eportfolio.server.model.UserComment;

import java.util.List;
import java.util.Optional;

public interface UserCommentService {

    UserComment comment(User user, Portfolio portfolio, String comment);

    void uncomment(User user, String id);

//    Optional<UserComment> findByUsernameAndPortfolioId(String username, String portfolioId);

    UserComment delete(UserComment userLike);

    Optional<UserComment> findByUsernameAndId(String username, String id);

    List<UserComment> findByUser(User user);

    List<UserComment> findAllCommentsMade(String username);

    List<UserComment> findByPortfolio(Portfolio portfolio);
}
