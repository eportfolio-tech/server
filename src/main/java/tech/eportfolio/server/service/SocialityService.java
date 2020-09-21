package tech.eportfolio.server.service;

import tech.eportfolio.server.model.UserLike;

import java.util.List;
import java.util.Optional;

public interface SocialityService {

    List<UserLike> findAllLiked(String username);

    UserLike likePortfolio(String username, String portfolioId);

    UserLike unlikePortfolio(String username, String portfolioId);


    Optional<UserLike> findByUsernameAndPortfolioId(String username, String portfolioId);

}
