package tech.eportfolio.server.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.eportfolio.server.model.Portfolio;
import tech.eportfolio.server.model.User;
import tech.eportfolio.server.model.UserComment;
import tech.eportfolio.server.repository.UserCommentRepository;
import tech.eportfolio.server.service.UserCommentService;
import tech.eportfolio.server.service.UserService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserCommentServiceImpl implements UserCommentService {

    private final UserCommentRepository userCommentRepository;

    private final UserService userService;

    @Autowired
    public UserCommentServiceImpl(UserCommentRepository userCommentRepository, UserService userService) {
        this.userCommentRepository = userCommentRepository;
        this.userService = userService;
    }

    @Override
    public List<UserComment> findByPortfolio(Portfolio portfolio) {
        return userCommentRepository.findByPortfolioIdAndDeleted(portfolio.getId(), false);
    }

    @Override
    public List<User> findUsersByUserComments(List<UserComment> userComments) {
        return userService.findByUsernameIn(userComments.stream().map(UserComment::getUsername).collect(Collectors.toList()));
    }

    @Override
    public UserComment reply(User user, UserComment parent, String content) {
        UserComment userComment = new UserComment();
        userComment.setUsername(user.getUsername());
        userComment.setPortfolioId(parent.getPortfolioId());
        userComment.setContent(content);
        userComment.setParentId(parent.getId());
        return userCommentRepository.save(userComment);
    }

    @Override
    public Optional<UserComment> findById(String id) {
        return userCommentRepository.findById(id);
    }


    @Override
    public UserComment create(User user, Portfolio portfolio, String content) {
        UserComment userComment = new UserComment();
        userComment.setUsername(user.getUsername());
        userComment.setPortfolioId(portfolio.getId());
        userComment.setContent(content);
        userComment.setParentId(null);
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
