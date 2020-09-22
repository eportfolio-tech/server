package tech.eportfolio.server.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.eportfolio.server.common.exception.UserLikeExistException;
import tech.eportfolio.server.common.exception.UserLikeNotExistException;
import tech.eportfolio.server.model.Portfolio;
import tech.eportfolio.server.model.User;
import tech.eportfolio.server.model.UserLike;
import tech.eportfolio.server.repository.mongodb.UserLikeRepository;
import tech.eportfolio.server.service.PortfolioService;
import tech.eportfolio.server.service.UserLikeService;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class UserLikeServiceImpl implements UserLikeService {

    private final UserLikeRepository userLikeRepository;

    private final PortfolioService portfolioService;

    @Autowired
    public UserLikeServiceImpl(UserLikeRepository userLikeRepository, PortfolioService portfolioService) {
        this.userLikeRepository = userLikeRepository;
        this.portfolioService = portfolioService;
    }


    @Override
    public List<UserLike> findByUser(User user) {
        return userLikeRepository.findByUsernameAndDeleted(user.getUsername(), false);
    }

    @Override
    public List<UserLike> findByPortfolio(Portfolio portfolio) {
        return userLikeRepository.findByPortfolioIdAndDeleted(portfolio.getId(), false);
    }



    @Override
    public UserLike like(User user, Portfolio portfolio) {
        Optional<UserLike> liker = this.findByUsernameAndPortfolioId(user.getUsername(), portfolio.getId());
        if (liker.isPresent()) {
            UserLike userLike = liker.get();
            if (userLike.isDeleted()) {
                userLike.setDeleted(false);
                userLike.setCreatedDate(new Date());
                return userLikeRepository.save(userLike);
            } else {
                throw new UserLikeExistException(user.getUsername(), portfolio.getId());
            }
        }
        UserLike newUserLike = new UserLike();
        newUserLike.setUsername(user.getUsername());
        newUserLike.setPortfolioId(portfolio.getId());
        return userLikeRepository.save(newUserLike);
    }

    @Override
    public UserLike unlike(User user, Portfolio portfolio) {
        String username = user.getUsername();
        String portfolioId = portfolio.getId();
        UserLike userLike = this.findByUsernameAndPortfolioId(user.getUsername(), portfolio.getId()).orElseThrow(
                () -> new UserLikeNotExistException(username, portfolioId));
        if (userLike.isDeleted()) {
            throw new UserLikeNotExistException(username, portfolioId);
        }
        return this.delete(userLike);
    }

    @Override
    public Optional<UserLike> findByUsernameAndPortfolioId(String likerName, String portfolioId) {
        return Optional.ofNullable(userLikeRepository.findByUsernameAndPortfolioId(likerName, portfolioId));
    }

    @Override
    public UserLike delete(UserLike userLike){
        userLike.setDeleted(true);
        userLikeRepository.save(userLike);
        return userLike;
    }
}
