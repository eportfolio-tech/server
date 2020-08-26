package tech.eportfolio.server.repository;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import tech.eportfolio.server.model.User;

public interface UserRepository extends CrudRepository<User, Long>{
    List<User> findByLastName(String lastName);
    User findById(long id);
}
