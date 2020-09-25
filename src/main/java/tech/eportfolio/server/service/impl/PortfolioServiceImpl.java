package tech.eportfolio.server.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.core.query.TextQuery;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;
import tech.eportfolio.server.common.constant.Visibility;
import tech.eportfolio.server.common.utility.NullAwareBeanUtilsBean;
import tech.eportfolio.server.dto.PortfolioDTO;
import tech.eportfolio.server.model.Portfolio;
import tech.eportfolio.server.model.User;
import tech.eportfolio.server.repository.PortfolioRepository;
import tech.eportfolio.server.service.PortfolioService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class PortfolioServiceImpl implements PortfolioService {

    private final PortfolioRepository portfolioRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    public PortfolioServiceImpl(PortfolioRepository portfolioRepository) {
        this.portfolioRepository = portfolioRepository;
    }

    @Override
    public Optional<Portfolio> findByUsername(String username) {
        return Optional.ofNullable(portfolioRepository.findByUsername(username));
    }

    @Override
    public Portfolio fromPortfolioDTO(PortfolioDTO portfolioDTO) {
        Portfolio portfolio = new Portfolio();
        NullAwareBeanUtilsBean.copyProperties(portfolioDTO, portfolio);
        return portfolio;
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
    public Page<Portfolio> searchWithPaginationAndVisibilities(String text, Pageable pageable, List<Visibility> visibilities) {
        Query query = TextQuery.queryText(new TextCriteria().matchingAny(text)).sortByScore()
                .addCriteria(Criteria.where("visibility").in(visibilities)).with(pageable);
        List<Portfolio> result = mongoTemplate.find(query, Portfolio.class);
        return PageableExecutionUtils.getPage(
                result,
                pageable,
                () -> mongoTemplate.count(Query.of(query).limit(-1).skip(-1), Portfolio.class));
    }


    @Override
    public List<Portfolio> searchWithVisibilities(String text, List<Visibility> visibilities) {
        Query query = TextQuery.queryText(new TextCriteria().matchingAny(text)).sortByScore()
                .addCriteria(Criteria.where("visibility").in(visibilities));
        return mongoTemplate.find(query, Portfolio.class);
    }

    @Override
    public Portfolio updateContent(Portfolio portfolio, Map<String, Object> map) {
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<HashMap<String, Object>> typeRef = new TypeReference<>() {
        };
        portfolio.setContent(new BasicDBObject(mapper.convertValue(map, typeRef)));
        return this.save(portfolio);
    }

    @Override
    public Portfolio deleteContent(Portfolio portfolio) {
        portfolio.setContent(null);
        return this.save(portfolio);
    }

    @Override
    public Portfolio save(Portfolio portfolio) {
        return portfolioRepository.save(portfolio);
    }


}
