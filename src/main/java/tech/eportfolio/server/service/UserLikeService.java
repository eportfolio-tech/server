package tech.eportfolio.server.service;

import tech.eportfolio.server.model.Portfolio;
import tech.eportfolio.server.model.User;
import tech.eportfolio.server.model.UserLike;

import java.util.List;
import java.util.Optional;

public interface UserLikeService {

    UserLike like(User user, Portfolio portfolio);

    UserLike unlike(User user, Portfolio portfolio);

    Optional<UserLike> findByUsernameAndPortfolioId(String username, String portfolioId);

    UserLike delete(UserLike userLike);

    List<UserLike> findByUser(User user);

    List<UserLike> findByPortfolio(Portfolio portfolio);
}
