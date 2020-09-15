package tech.eportfolio.server.repository;

import org.springframework.data.repository.CrudRepository;
import tech.eportfolio.server.model.UserStorageContainer.User;

public interface UserRepository extends CrudRepository<User, Long> {

    User findById(long id);

    User findByUsernameAndDeleted(String username, boolean deleted);

    User findByEmailAndDeleted(String email, boolean deleted);
}
