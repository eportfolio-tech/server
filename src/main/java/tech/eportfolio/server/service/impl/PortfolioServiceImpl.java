package tech.eportfolio.server.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.core.query.TextQuery;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;
import tech.eportfolio.server.common.constant.FeedType;
import tech.eportfolio.server.common.constant.ParentType;
import tech.eportfolio.server.common.constant.Visibility;
import tech.eportfolio.server.common.utility.NullAwareBeanUtilsBean;
import tech.eportfolio.server.dto.PortfolioDTO;
import tech.eportfolio.server.model.Activity;
import tech.eportfolio.server.model.Portfolio;
import tech.eportfolio.server.model.User;
import tech.eportfolio.server.repository.PortfolioRepository;
import tech.eportfolio.server.service.ActivityService;
import tech.eportfolio.server.service.PortfolioService;
import tech.eportfolio.server.service.UserFollowService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Qualifier("PortfolioServiceImpl")
public class PortfolioServiceImpl implements PortfolioService {

    private final PortfolioRepository portfolioRepository;

    private static final String VISIBILITY = "visibility";

    @Autowired
    private UserFollowService userFollowService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private ActivityService activityService;

    @Autowired
    public PortfolioServiceImpl(PortfolioRepository portfolioRepository) {
        this.portfolioRepository = portfolioRepository;
    }

    @Override
    public Optional<Portfolio> findByUsername(String username) {
        return portfolioRepository.findByUsername(username);
    }

    @Override
    public List<Portfolio> findByUserIdIn(List<String> userIds) {
        return portfolioRepository.findByUserIdInAndDeleted(userIds, false);
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
        toCreate.setCoverImage(portfolio.getCoverImage());
        Portfolio created = portfolioRepository.save(toCreate);
        pushPortfolioToActivity(created);
        return created;
    }

    @Override
    public Page<Portfolio> searchByKeywordWithPaginationAndVisibilities(String text, Pageable pageable, List<Visibility> visibilities) {
        Query query = TextQuery.queryText(new TextCriteria().matchingAny(text)).sortByScore()
                .addCriteria(Criteria.where(VISIBILITY).in(visibilities)).with(pageable);
        return this.searchWithPagination(query, pageable);
    }

    @Override
    public Page<Portfolio> searchByTagWithPaginationAndVisibilities(Pageable pageable, List<Visibility> visibilities, List<String> userIds) {
        Query query = new Query()
                .addCriteria(Criteria.where("userId").in(userIds))
                .addCriteria(Criteria.where(VISIBILITY).in(visibilities)).with(pageable);
        return this.searchWithPagination(query, pageable);
    }

    @Override
    public Page<Portfolio> searchWithPagination(Query query, Pageable pageable) {
        List<Portfolio> result = mongoTemplate.find(query, Portfolio.class);
        return PageableExecutionUtils.getPage(
                result,
                pageable,
                () -> mongoTemplate.count(Query.of(query).limit(-1).skip(-1), Portfolio.class));
    }


    @Override
    public List<Portfolio> searchWithVisibilities(String text, List<Visibility> visibilities) {
        Query query = TextQuery.queryText(new TextCriteria().matchingAny(text)).sortByScore()
                .addCriteria(Criteria.where(VISIBILITY).in(visibilities));
        return mongoTemplate.find(query, Portfolio.class);
    }

    @Override
    public List<Portfolio> searchWithVisibilities(List<Visibility> visibilities) {
        Query query = new Query()
                .addCriteria(Criteria.where(VISIBILITY).in(visibilities));
        return mongoTemplate.find(query, Portfolio.class);
    }

    @Override
    public List<Portfolio> findByIdIn(List<String> ids) {
        return portfolioRepository.findByIdInAndDeleted(ids, false);
    }

    @Override
    public Activity toUpdateActivity(Portfolio portfolio) {
        return Activity.builder()
                .feedType(FeedType.UPDATE)
                .parentType(ParentType.PORTFOLIO)
                .parentId(portfolio.getId())
                .username(portfolio.getUsername())
                .deleted(false).build();
    }

    @Override
    public Activity toPortfolioActivity(Portfolio portfolio) {
        return Activity.builder()
                .feedType(FeedType.PORTFOLIO)
                .parentType(ParentType.PORTFOLIO)
                .parentId(portfolio.getId())
                .username(portfolio.getUsername())
                .deleted(false).build();
    }

    @Override
    public Portfolio updateContent(Portfolio portfolio, Map<String, Object> map) {
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<HashMap<String, Object>> typeRef = new TypeReference<>() {
        };
        portfolio.setContent(new BasicDBObject(mapper.convertValue(map, typeRef)));

        // Create an update for portfolio
        Activity update = pushUpdateToActivity(portfolio);
        // Push the activity to followers
        userFollowService.sendActivityToFollowers(update, portfolio.getUsername());
        return this.save(portfolio);
    }

    @Override
    public Portfolio deleteContent(Portfolio portfolio) {
        portfolio.setContent(null);
        return this.save(portfolio);
    }

    @Override
    public Portfolio updatePortfolio(Portfolio portfolio, PortfolioDTO portfolioDTO) {
        NullAwareBeanUtilsBean.copyProperties(portfolioDTO, portfolio);
        return this.save(portfolio);
    }

    @Override
    public Activity pushUpdateToActivity(Portfolio portfolio) {
        // Create an new activity for the portfolio update
        Activity updateActivity = toUpdateActivity(portfolio);
        return activityService.save(updateActivity);
    }

    @Override
    public List<Activity> pushUpdateToActivity(List<Portfolio> portfolios) {
        return portfolios.stream().map(this::toUpdateActivity).collect(Collectors.toList());
    }

    @Override
    public List<Activity> pushPortfolioToActivity(List<Portfolio> portfolios) {
        return activityService.saveAll(portfolios.stream().map(this::toPortfolioActivity).collect(Collectors.toList()));
    }

    @Override
    public Activity pushPortfolioToActivity(Portfolio portfolio) {
        Activity activity = toPortfolioActivity(portfolio);
        return activityService.save(activity);
    }

    @Override
    public Portfolio save(Portfolio portfolio) {
        return portfolioRepository.save(portfolio);
    }

    @Override
    public List<Portfolio> saveAll(List<Portfolio> portfolios) {
        return portfolioRepository.saveAll(portfolios);
    }
}
