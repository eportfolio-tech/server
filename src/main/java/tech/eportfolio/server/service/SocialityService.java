package tech.eportfolio.server.service;

import tech.eportfolio.server.model.UserLike;

import java.util.Optional;

public interface SocialityService {

    UserLike likePortfolio(String username, String portfolioId);


    Optional<UserLike> findByUsernameAndPortfolioId(String username, String portfolioId);

}
