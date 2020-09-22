package tech.eportfolio.server.repository.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import tech.eportfolio.server.model.UserComment;

import java.util.List;

@Repository
public interface UserCommentRepository extends MongoRepository<UserComment, String> {

    UserComment findByUsernameAndId(String username, String id);

    List<UserComment> findByUsernameAndDeleted(String username, boolean deleted);

    List<UserComment> findByUsername(String username);

    List<UserComment> findByPortfolioId(String portfolioId);

}
