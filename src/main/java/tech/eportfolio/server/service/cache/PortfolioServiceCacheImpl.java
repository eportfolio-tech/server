package tech.eportfolio.server.service.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import tech.eportfolio.server.common.constant.Visibility;
import tech.eportfolio.server.common.exception.PortfolioNotFoundException;
import tech.eportfolio.server.dto.PortfolioDTO;
import tech.eportfolio.server.model.Portfolio;
import tech.eportfolio.server.model.User;
import tech.eportfolio.server.service.PortfolioService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@CacheConfig(cacheNames = "portfolios")
@Qualifier("PortfolioServiceCacheImpl")
@Primary
public class PortfolioServiceCacheImpl implements PortfolioService {

    private final PortfolioService portfolioService;

    @Autowired
    public PortfolioServiceCacheImpl(@Qualifier("PortfolioServiceImpl") PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @Override
    public Optional<Portfolio> findByUsername(String username) {
        return portfolioService.findByUsername(username);
    }

    /**
     * This is a wrapper method to wrap Optional<Portfolio> findByUsername(String username)
     * The reason is that, the previous way made Redis store cache even if the portfolio is not present
     * A dirty data will then be written in. It may then fail the process of creating the portfolio
     *
     * @param username
     * @return
     */
    @Override
    @Cacheable
    public Portfolio foundPortfolioByUsername(String username) {
        return this.findByUsername(username).orElseThrow(() -> new PortfolioNotFoundException(username));
    }

    @Override
    public List<Portfolio> findByUserIdIn(List<String> userIds) {
        return portfolioService.findByUserIdIn(userIds);
    }

    @Override
    public Portfolio fromPortfolioDTO(PortfolioDTO portfolioDTO) {
        return portfolioService.fromPortfolioDTO(portfolioDTO);
    }

    @Override
    public Portfolio create(User user, Portfolio portfolio) {
        return portfolioService.create(user, portfolio);
    }

    @Override
//    @Cacheable
    public Page<Portfolio> searchByKeywordWithPaginationAndVisibilities(String text, Pageable pageable, List<Visibility> visibilities) {
        return portfolioService.searchByKeywordWithPaginationAndVisibilities(text, pageable, visibilities);
    }

    @Override
//    @Cacheable(key = "#userIds")
    public Page<Portfolio> searchByTagWithPaginationAndVisibilities(Pageable pageable, List<Visibility> visibilities, List<String> userIds) {
        return portfolioService.searchByTagWithPaginationAndVisibilities(pageable, visibilities, userIds);
    }

    @Override
    public Page<Portfolio> searchWithPagination(Query query, Pageable pageable) {
        return portfolioService.searchWithPagination(query, pageable);
    }


    @Override
    public List<Portfolio> searchWithVisibilities(String text, List<Visibility> visibilities) {
        return portfolioService.searchWithVisibilities(text, visibilities);
    }

    @Override
    public List<Portfolio> searchWithVisibilities(List<Visibility> visibilities) {
        return portfolioService.searchWithVisibilities(visibilities);
    }

    @Override
    @CachePut(key = "#portfolio.username")
    public Portfolio updateContent(Portfolio portfolio, Map<String, Object> map) {
        return portfolioService.updateContent(portfolio, map);
    }

    @Override
    @CachePut(key = "#portfolio.username")
    public Portfolio deleteContent(Portfolio portfolio) {
        return portfolioService.deleteContent(portfolio);
    }

    @Override
    @CachePut(key = "#portfolio.username")
    public Portfolio updatePortfolio(Portfolio portfolio, PortfolioDTO portfolioDTO) {
        return portfolioService.updatePortfolio(portfolio, portfolioDTO);
    }

    @Override
    public Portfolio save(Portfolio portfolio) {
        return portfolioService.save(portfolio);
    }


}
