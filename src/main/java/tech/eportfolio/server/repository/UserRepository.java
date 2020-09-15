package tech.eportfolio.server.repository;

import org.springframework.data.repository.CrudRepository;
import tech.eportfolio.server.model.User;

public interface UserRepository extends CrudRepository<User, Long> {

    User findById(long id);

    User findByUsernameAndDeleted(String username, boolean deleted);

    User findByEmailAndDeleted(String email, boolean deleted);
}
