package tech.eportfolio.server.service.impl;

import ma.glasnost.orika.BoundMapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.core.query.TextQuery;
import org.springframework.stereotype.Service;
import tech.eportfolio.server.dto.PortfolioDTO;
import tech.eportfolio.server.model.Portfolio;
import tech.eportfolio.server.model.User;
import tech.eportfolio.server.repository.mongodb.PortfolioRepository;
import tech.eportfolio.server.service.PortfolioService;

import java.util.List;
import java.util.Optional;

@Service
public class PortfolioServiceImpl implements PortfolioService {

    private final PortfolioRepository portfolioRepository;

    private BoundMapperFacade<PortfolioDTO, Portfolio> boundMapper;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    public PortfolioServiceImpl(PortfolioRepository portfolioRepository) {
        this.portfolioRepository = portfolioRepository;
    }

    @Autowired
    public void setBoundMapper(BoundMapperFacade<PortfolioDTO, Portfolio> boundMapper) {
        this.boundMapper = boundMapper;
    }

    // TODO: Need to distinguish with the boundMapperFacade in UserServiceImpl
    @Bean
    public BoundMapperFacade<PortfolioDTO, Portfolio> boundMapperFacade2() {
        MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
        return mapperFactory.getMapperFacade(PortfolioDTO.class, Portfolio.class);
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
    public Portfolio fromPortfolioDTO(PortfolioDTO portfolioDTO) {
        return boundMapper.map(portfolioDTO);
    }

    @Override
    public Portfolio create(User user, Portfolio portfolio) {
        // Set attributes for eportfolio
        Portfolio toCreate = new Portfolio();
        toCreate.setDescription(portfolio.getDescription());
        toCreate.setTitle(portfolio.getTitle());
        toCreate.setVisibility(portfolio.getVisibility());
        toCreate.setUsername(user.getUsername());
        toCreate.setUserId(user.getId());
        return portfolioRepository.save(toCreate);
    }

    @Override
    public Page<Portfolio> searchWithPagination(String text, Pageable pageable) {
        Query query = TextQuery.queryText(new TextCriteria().matchingAny(text)).sortByScore().with(pageable);
        List<Portfolio> result = mongoTemplate.find(query, Portfolio.class);
        long count = mongoTemplate.count(query, Portfolio.class);
        return new PageImpl<>(result, pageable, count);
    }

    @Override
    public Portfolio save(Portfolio portfolio) {
        return portfolioRepository.save(portfolio);
    }


}
