package tech.eportfolio.server.repository.jpa;

import org.springframework.data.repository.CrudRepository;
import tech.eportfolio.server.model.User;

import java.util.Date;
import java.util.List;

public interface UserRepository extends CrudRepository<User, Long> {

    User findById(long id);

    User findByUsernameAndDeleted(String username, boolean deleted);

    User findByEmailAndDeleted(String email, boolean deleted);

    List<User> findByDeletedAndUpdatedDateBeforeAndBlobUUIDIsNotNull(boolean deleted, Date updateDate);
}
