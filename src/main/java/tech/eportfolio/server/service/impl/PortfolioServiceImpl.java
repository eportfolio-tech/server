package tech.eportfolio.server.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import tech.eportfolio.server.model.Portfolio;
import tech.eportfolio.server.repository.mongodb.PortfolioRepository;
import tech.eportfolio.server.service.PortfolioService;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Service
public class PortfolioServiceImpl implements PortfolioService {

    private final PortfolioRepository portfolioRepository;

    @Autowired
    private MongoOperations mongoOperations;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public PortfolioServiceImpl(PortfolioRepository portfolioRepository) {
        this.portfolioRepository = portfolioRepository;
    }

    @Override
    public Optional<Portfolio> findById(String id) {
        return Optional.ofNullable(portfolioRepository.findByIdAndDeleted(id, false));
    }

    @Override
    public Optional<Portfolio> findByUserId(Long userId) {
        return Optional.ofNullable(portfolioRepository.findByUserIdAndDeleted(userId, false));
    }

    @Override
    public List<Portfolio> findByUserIdIn(List<Long> userIds) {
        return portfolioRepository.findByUserIdInAndDeleted(userIds, false);
    }

    @Override
    public Optional<Portfolio> findByUsername(String username) {
        return Optional.ofNullable(portfolioRepository.findByUsername(username));
    }

    @Override
    public Page<Portfolio> searchWithPagination(String text, Pageable pageable) {
        Query query = new Query();
        query.with(pageable);
        // find the text pattern in certain fields
        Criteria c1 = Criteria.where("title").regex(".*" + text + ".*");
        Criteria c2 = Criteria.where("username").regex(".*" + text + ".*");
        Criteria c3 = Criteria.where("description").regex(".*" + text + ".*");
        query.addCriteria(new Criteria().orOperator(c1, c2, c3));
        List<Portfolio> list = mongoOperations.find(query, Portfolio.class);
        long count = mongoOperations.count(query, Portfolio.class);
        return new PageImpl<>(list, pageable, count);
    }

    @Override
    public Portfolio save(Portfolio portfolio) {
        return portfolioRepository.save(portfolio);
    }


}
