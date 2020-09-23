package tech.eportfolio.server.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import tech.eportfolio.server.model.User;

import java.util.Date;
import java.util.List;

@Repository

public interface UserRepository extends MongoRepository<User, String> {

    User findByUsernameAndDeleted(String username, boolean deleted);

    User findByEmailAndDeleted(String email, boolean deleted);

    List<User> findByDeletedAndUpdatedDateBeforeAndBlobUUIDIsNotNull(boolean deleted, Date updateDate);
}
