package tech.eportfolio.server.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.eportfolio.server.common.exception.UserLikeExistException;
import tech.eportfolio.server.common.exception.UserLikeNotExistException;
import tech.eportfolio.server.model.UserLike;
import tech.eportfolio.server.repository.mongodb.PortfolioRepository;
import tech.eportfolio.server.repository.mongodb.UserLikeRepository;
import tech.eportfolio.server.service.SocialityService;

import java.util.List;
import java.util.Optional;

@Service
public class SocialityServiceImpl implements SocialityService {

    private final UserLikeRepository userLikeRepository;

    private final PortfolioRepository portfolioRepository;


    @Autowired
    public SocialityServiceImpl(PortfolioRepository portfolioRepository, UserLikeRepository userLikeRepository) {
        this.portfolioRepository = portfolioRepository;
        this.userLikeRepository = userLikeRepository;
    }


    @Override
    public List<UserLike> findAllLiked(String username) {
        return userLikeRepository.findByUsername(username);
    }

    @Override
    public UserLike likePortfolio(String username, String portfolioId) {

        Optional<UserLike> userLike = this.findByUsernameAndPortfolioId(username, portfolioId);
        if (userLike.isPresent()) {
            throw new UserLikeExistException(username, portfolioId);
        }
        UserLike newUserLike = new UserLike();
        newUserLike.setUsername(username);
        newUserLike.setPortfolioId(portfolioId);
        userLikeRepository.save(newUserLike);
        return newUserLike;
    }

    @Override
    public UserLike unlikePortfolio(String username, String portfolioId) {
        Optional<UserLike> userLike = this.findByUsernameAndPortfolioId(username, portfolioId);
        if (userLike.isEmpty()) {
            throw new UserLikeNotExistException(username, portfolioId);
        }
        return userLikeRepository.deleteByUsernameAndPortfolioId(username, portfolioId);
    }

    @Override
    public Optional<UserLike> findByUsernameAndPortfolioId(String username, String portfolioId) {
        return Optional.ofNullable(userLikeRepository.findByUsernameAndPortfolioId(username, portfolioId));
    }


}
