package tech.eportfolio.server.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import tech.eportfolio.server.model.Portfolio;

import java.util.List;

@Repository
public interface PortfolioRepository extends MongoRepository<Portfolio, String> {

    List<Portfolio> findByUserIdInAndDeleted(List<String> userIds, boolean b);

    Portfolio findByUsername(String username);

}
