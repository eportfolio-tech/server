package tech.eportfolio.server.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.eportfolio.server.common.exception.UserDidNotBeCommentedException;
import tech.eportfolio.server.common.exception.UserDidNotCommentException;
import tech.eportfolio.server.model.Portfolio;
import tech.eportfolio.server.model.User;
import tech.eportfolio.server.model.UserComment;
import tech.eportfolio.server.repository.mongodb.UserCommentRepository;
import tech.eportfolio.server.service.UserCommentService;

import java.util.List;
import java.util.Optional;

@Service
public class UserCommentServiceImpl implements UserCommentService {

    private final UserCommentRepository userCommentRepository;

    @Autowired
    public UserCommentServiceImpl(UserCommentRepository userCommentRepository) {
        this.userCommentRepository = userCommentRepository;
    }


    @Override
    public List<UserComment> findByUser(User user) {
        return userCommentRepository.findByUsernameAndDeleted(user.getUsername(), false);
    }

    @Override
    public List<UserComment> findAllCommentsMade(String username) {
        return userCommentRepository.findByUsername(username);
    }

    @Override
    public List<UserComment> findByPortfolio(Portfolio portfolio) {
        return userCommentRepository.findByPortfolioId(portfolio.getId());
    }


    @Override
    public UserComment comment(User user, Portfolio portfolio, String comment) {
        UserComment userComment = new UserComment();
        userComment.setUsername(user.getUsername());
        userComment.setPortfolioId(portfolio.getId());
        userComment.setComment(comment);
        return userCommentRepository.save(userComment);
    }

    @Override
    public void uncomment(User user, String id) {
        UserComment userComment = this.findByUsernameAndId(user.getUsername(), id)
                .orElseThrow(() -> new UserDidNotCommentException(user.getUsername(), id));
        userCommentRepository.delete(userComment);
    }

    @Override
    public void deleteComment(Portfolio portfolio, String id) {
        UserComment userComment = this.findByPortfolioIdAndId(portfolio.getId(), id)
                .orElseThrow(() -> new UserDidNotBeCommentedException(portfolio.getUsername(), id));
        userCommentRepository.delete(userComment);
    }

    @Override
    public Optional<UserComment> findByUsernameAndId(String username, String id) {
        return Optional.ofNullable(userCommentRepository.findByUsernameAndId(username, id));
    }

    @Override
    public Optional<UserComment> findByPortfolioIdAndId(String portfolioId, String id) {
        return Optional.ofNullable(userCommentRepository.findByPortfolioIdAndId(portfolioId, id));
    }

    @Override
    public UserComment delete(UserComment userLike) {
        userLike.setDeleted(true);
        userCommentRepository.save(userLike);
        return userLike;
    }
}
