package tech.eportfolio.server.service;

import tech.eportfolio.server.dto.UserDTO;
import tech.eportfolio.server.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User register(User user);

    Optional<User> findById(long id);

    List<User> findAll();

    Optional<User> findByEmail(String email);

    User fromUserDTO(UserDTO userDTO);

    Optional<User> findByUsername(String username);
}
