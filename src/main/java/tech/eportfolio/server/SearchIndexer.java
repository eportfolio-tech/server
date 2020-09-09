package tech.eportfolio.server;

import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

@Component
public class SearchIndexer implements ApplicationListener<ApplicationReadyEvent> {

    Logger logger = LoggerFactory.getLogger(SearchIndexer.class);


    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public void onApplicationEvent(ApplicationReadyEvent event) {
        try {
            FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
            fullTextEntityManager.createIndexer().startAndWait();
        } catch (InterruptedException ex) {
            logger.error("Error creating indexer", ex);
        }
    }

}