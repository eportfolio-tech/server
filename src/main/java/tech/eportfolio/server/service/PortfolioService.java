package tech.eportfolio.server.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Query;
import tech.eportfolio.server.common.constant.Visibility;
import tech.eportfolio.server.dto.PortfolioDTO;
import tech.eportfolio.server.model.Portfolio;
import tech.eportfolio.server.model.User;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface PortfolioService {

    Optional<Portfolio> findByUsername(String username);

    Portfolio foundPortfolioByUsername(String username);

    List<Portfolio> findByUserIdIn(List<String> userIds);

    Portfolio fromPortfolioDTO(PortfolioDTO portfolioDTO);

    Portfolio create(User user, Portfolio portfolio);

    Page<Portfolio> searchByKeywordWithPaginationAndVisibilities(String text, Pageable pageable, List<Visibility> visibilities);

    Page<Portfolio> searchByTagWithPaginationAndVisibilities(Pageable pageable, List<Visibility> visibilities, List<String> userIds);

    Page<Portfolio> searchWithPagination(Query query, Pageable pageable);

    Portfolio updateContent(Portfolio portfolio, Map<String, Object> map);

    Portfolio deleteContent(Portfolio portfolio);

    Portfolio updatePortfolio(Portfolio portfolio, PortfolioDTO portfolioDTO);

    Portfolio save(Portfolio portfolio);

    List<Portfolio> searchWithVisibilities(String text, List<Visibility> visibilities);

    List<Portfolio> searchWithVisibilities(List<Visibility> visibilities);
}
