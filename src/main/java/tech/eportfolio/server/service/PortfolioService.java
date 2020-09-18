package tech.eportfolio.server.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import tech.eportfolio.server.dto.PortfolioDTO;
import tech.eportfolio.server.model.Portfolio;
import tech.eportfolio.server.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public interface PortfolioService {
    Optional<Portfolio> findById(String id);

    Optional<Portfolio> findByUserId(Long userId);

    List<Portfolio> findByUserIdIn(List<Long> userIds);

    Optional<Portfolio> findByUsername(String username);

    Portfolio fromPortfolioDTO(PortfolioDTO portfolioDTO);

    PortfolioDTO toPortfolioDTO(Portfolio portfolio);

    Portfolio create(User user, Portfolio portfolio);

    Page<Portfolio> searchWithPagination(String text, Pageable pageable);

    Portfolio updateContent(Portfolio portfolio, HashMap<String, Object> map);

    Portfolio deleteContent(Portfolio portfolio);

    Portfolio save(Portfolio portfolio);
}
