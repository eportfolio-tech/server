package tech.eportfolio.server.repository;

import org.springframework.data.repository.CrudRepository;
import tech.eportfolio.server.model.Portfolio;

import java.util.List;

public interface PortfolioRepository extends CrudRepository<Portfolio, Long> {
    Portfolio findByIdAndDeleted(long id, boolean deleted);

    Portfolio findByUserIdAndDeleted(Long userId, boolean deleted);

    List<Portfolio> findByUserIdInAndDeleted(List<Long> userIds, boolean deleted);

}
