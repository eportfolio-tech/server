package tech.eportfolio.server.service;

import com.mongodb.BasicDBObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import tech.eportfolio.server.common.constant.Visibility;
import tech.eportfolio.server.dto.PortfolioDTO;
import tech.eportfolio.server.model.Portfolio;
import tech.eportfolio.server.model.User;

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

    Page<Portfolio> searchWithPaginationAndVisibilities(String text, Pageable pageable, List<Visibility> visibilities);

    Portfolio updateContent(Portfolio portfolio, BasicDBObject json);

    Portfolio deleteContent(Portfolio portfolio);

    Portfolio save(Portfolio portfolio);

    List<Portfolio> search(String text);

    List<Portfolio> searchWithVisibilities(String text, List<Visibility> visibilities);
}
