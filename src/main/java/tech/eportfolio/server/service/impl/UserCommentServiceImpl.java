package tech.eportfolio.server.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.eportfolio.server.model.Portfolio;
import tech.eportfolio.server.model.User;
import tech.eportfolio.server.model.UserComment;
import tech.eportfolio.server.repository.UserCommentRepository;
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
    public List<UserComment> findByPortfolio(Portfolio portfolio) {
        return userCommentRepository.findByPortfolioIdAndDeleted(portfolio.getId(), false);
    }


    @Override
    public UserComment create(User user, Portfolio portfolio, String comment) {
        UserComment userComment = new UserComment();
        userComment.setUsername(user.getUsername());
        userComment.setPortfolioId(portfolio.getId());
        userComment.setComment(comment);
        return userCommentRepository.save(userComment);
    }

    @Override
    public UserComment delete(UserComment userComment) {
        userComment.setDeleted(true);
        return userCommentRepository.save(userComment);
    }

    @Override
    public Optional<UserComment> findByUsernameAndIdAndDeleted(String username, String id, boolean deleted) {
        return Optional.ofNullable(userCommentRepository.findByUsernameAndIdAndDeleted(username, id, deleted));
    }

}
