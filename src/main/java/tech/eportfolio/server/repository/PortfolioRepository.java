package tech.eportfolio.server.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import tech.eportfolio.server.model.UserStorageContainer.Portfolio;

import java.util.List;

public interface PortfolioRepository extends PagingAndSortingRepository<Portfolio, Long> {
    Portfolio findByIdAndDeleted(long id, boolean deleted);

    Portfolio findByUserIdAndDeleted(Long userId, boolean deleted);

    List<Portfolio> findByUserIdInAndDeleted(List<Long> userIds, boolean deleted);

    Portfolio findByUsername(String username);

}
