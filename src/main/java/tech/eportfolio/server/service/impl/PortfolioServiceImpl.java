package tech.eportfolio.server.service.impl;

import org.apache.lucene.search.Query;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import tech.eportfolio.server.exception.UserNotFoundException;
import tech.eportfolio.server.model.Portfolio;
import tech.eportfolio.server.model.User;
import tech.eportfolio.server.repository.PortfolioRepository;
import tech.eportfolio.server.service.PortfolioService;
import tech.eportfolio.server.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class PortfolioServiceImpl implements PortfolioService {

    private final PortfolioRepository portfolioRepository;

    private final UserService userService;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public PortfolioServiceImpl(PortfolioRepository portfolioRepository, UserService userService) {
        this.portfolioRepository = portfolioRepository;
        this.userService = userService;
    }

    @Override
    public Optional<Portfolio> findById(long id) {
        return Optional.ofNullable(portfolioRepository.findByIdAndDeleted(id, false));
    }

    @Override
    public Optional<Portfolio> findByUserId(Long userId) {
        return Optional.ofNullable(portfolioRepository.findByIdAndDeleted(userId, false));
    }

    @Override
    public List<Portfolio> findByUserIdIn(List<Long> userIds) {
        return portfolioRepository.findByUserIdInAndDeleted(userIds, false);
    }

    @Override
    public Optional<Portfolio> findByUsername(String username) {
        User user = userService.findByUsername(username).orElseThrow(() -> new UserNotFoundException(username));
        return findByUserId(user.getId());
    }


    @SuppressWarnings("unchecked")
    @Transactional
    public List<Portfolio> search(String text) {
        FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);

        QueryBuilder queryBuilder = fullTextEntityManager.getSearchFactory().buildQueryBuilder()
                .forEntity(Portfolio.class).get();

        Query query = queryBuilder
                .keyword()
                .fuzzy()
                .withEditDistanceUpTo(5)
                .withPrefixLength(0)
                .onFields("description", "title", "content", "createdBy")
                .matching(text)
                .createQuery();


        FullTextQuery jpaQuery = fullTextEntityManager.createFullTextQuery(query, Portfolio.class);
        return jpaQuery.getResultList();
    }

    @Transactional
    public Page<Portfolio> searchWithPagination(String text, Pageable pageable) {
        FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);

        /*
        Build a query builder from entityManager
         */
        QueryBuilder queryBuilder = fullTextEntityManager.getSearchFactory().buildQueryBuilder()
                .forEntity(Portfolio.class).get();

        Query query = queryBuilder
                .keyword()
                .fuzzy()
                // Allow max 5 edit distance
                .withEditDistanceUpTo(5)
                .withPrefixLength(0)
                // search these fields on Portfolio
                .onFields("description", "title", "content", "createdBy")
                .matching(text)
                .createQuery();

        FullTextQuery jpaQuery = fullTextEntityManager.createFullTextQuery(query, Portfolio.class);
        // Apply pagination to result
        jpaQuery.setFirstResult(pageable.getPageSize() * pageable.getPageNumber())
                .setMaxResults(pageable.getPageSize());

        @SuppressWarnings("unchecked")
        List<Portfolio> result = jpaQuery.getResultList();
        return new PageImpl<>(result, pageable, jpaQuery.getResultSize());
    }

    @Override
    public Portfolio save(Portfolio portfolio) {
        return portfolioRepository.save(portfolio);
    }


}
