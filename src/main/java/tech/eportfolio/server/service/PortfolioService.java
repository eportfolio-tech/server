package tech.eportfolio.server.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import tech.eportfolio.server.common.constant.Visibility;
import tech.eportfolio.server.dto.PortfolioDTO;
import tech.eportfolio.server.model.Portfolio;
import tech.eportfolio.server.model.User;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface PortfolioService {

    Optional<Portfolio> findByUsername(String username);

    Portfolio fromPortfolioDTO(PortfolioDTO portfolioDTO);

    Portfolio create(User user, Portfolio portfolio);

    Page<Portfolio> searchWithPaginationAndVisibilities(String text, Pageable pageable, List<Visibility> visibilities);

    Portfolio updateContent(Portfolio portfolio, Map<String, Object> map);

    Portfolio deleteContent(Portfolio portfolio);

    Portfolio save(Portfolio portfolio);

    List<Portfolio> searchWithVisibilities(String text, List<Visibility> visibilities);
}
