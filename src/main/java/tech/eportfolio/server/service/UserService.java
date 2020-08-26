package tech.eportfolio.server.service;

import tech.eportfolio.server.exception.UserNotFoundException;
import tech.eportfolio.server.model.User;

import java.util.List;

public interface UserService {
    User save(User user);

    User findById(long id) throws UserNotFoundException;

    List<User> findAll();
}
