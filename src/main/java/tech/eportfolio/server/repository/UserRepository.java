package tech.eportfolio.server.repository;

import org.springframework.data.repository.CrudRepository;
import tech.eportfolio.server.model.User;

import java.util.List;

public interface UserRepository extends CrudRepository<User, Long> {
    List<User> findByLastName(String lastName);

    User findById(long id);

    User findUserByUsername(String username);

    User findUserByEmail(String email);
}
