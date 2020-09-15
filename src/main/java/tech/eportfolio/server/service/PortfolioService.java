package tech.eportfolio.server.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import tech.eportfolio.server.model.UserStorageContainer.Portfolio;

import java.util.List;
import java.util.Optional;

public interface PortfolioService {
    Optional<Portfolio> findById(long id);

    Optional<Portfolio> findByUserId(Long userId);

    List<Portfolio> findByUserIdIn(List<Long> userIds);

    Optional<Portfolio> findByUsername(String username);

    List<Portfolio> search(String text);

    Page<Portfolio> searchWithPagination(String text, Pageable pageable);

    Portfolio save(Portfolio portfolio);
}
