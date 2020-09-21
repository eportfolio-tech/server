package tech.eportfolio.server.repository.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import tech.eportfolio.server.model.UserLike;

import java.util.List;

@Repository
public interface UserLikeRepository extends MongoRepository<UserLike, String> {

    List<UserLike> findByUsername(String username);

    UserLike findByUsernameAndPortfolioId(String username, String portfolioId);

    UserLike deleteByUsernameAndPortfolioId(String username, String portfolioId);

}
