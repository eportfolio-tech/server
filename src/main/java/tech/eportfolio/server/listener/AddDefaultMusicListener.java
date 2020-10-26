package tech.eportfolio.server.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import tech.eportfolio.server.model.Portfolio;
import tech.eportfolio.server.service.PortfolioService;

import java.util.List;

/**
 * AddDefaultMusicListener add default music to portfolio
 */
@Component
public class AddDefaultMusicListener {
    public static final String defaultMusicURL = "https://comp30002.blob.core.windows.net/image/cocabona,Glimlip-Drops.mp3";
    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private PortfolioService portfolioService;

    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent event) {
        Query query = new Query();
        query.addCriteria(Criteria.where("music").exists(false));
        List<Portfolio> portfolios = mongoTemplate.find(query, Portfolio.class);
        portfolios.forEach(e -> e.setMusic(defaultMusicURL));
        // Use portfolio service to save all updates, this should evict all cache in Redis
        portfolioService.saveAll(portfolios);
        logger.info("Add default music for {} portfolios", portfolios.size());
    }
}
