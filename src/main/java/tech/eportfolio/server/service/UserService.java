package tech.eportfolio.server.service;

import tech.eportfolio.server.dto.UserDTO;
import tech.eportfolio.server.model.User;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface UserService {
    User register(User user, boolean createAvatar);

    Optional<User> findByEmail(String email);

    List<User> findByIdIn(List<String> ids);

    User fromUserDTO(UserDTO userDTO);

    Optional<User> findByUsername(String username);

    boolean verifyPassword(User user, String password);

    String encodePassword(String raw);

    User changePassword(User user, String password);

    User save(User user);

    String createGithubAvatar(User user);

    void delete(User user);

    List<User> findDeletedUserWithContainer(Date deleteBeforeDate);

    List<User> saveAll(List<User> users);
}
