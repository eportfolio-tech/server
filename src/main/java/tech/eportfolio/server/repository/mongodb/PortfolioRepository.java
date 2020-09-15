package tech.eportfolio.server.repository.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import tech.eportfolio.server.model.Portfolio;

import java.util.List;

@Repository
public interface PortfolioRepository extends MongoRepository<Portfolio, String> {

    Portfolio findByIdAndDeleted(String id, boolean b);

    List<Portfolio> findByUserIdInAndDeleted(List<Long> userIds, boolean b);

    Portfolio findByUsername(String username);

    Portfolio findByUserIdAndDeleted(Long userId, boolean b);
}
