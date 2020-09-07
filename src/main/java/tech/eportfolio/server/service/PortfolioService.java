package tech.eportfolio.server.service;

import tech.eportfolio.server.model.Portfolio;

import java.util.List;

public interface PortfolioService {
    Portfolio findById(long id);

    Portfolio findByUserId(Long userId);

    List<Portfolio> findByUserIdIn(List<Long> userIds);

    Portfolio findByUsername(String username);
}
