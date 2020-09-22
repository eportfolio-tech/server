package tech.eportfolio.server.repository.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import tech.eportfolio.server.model.Template;

import java.util.List;

@Repository
public interface TemplateRepository extends MongoRepository<Template, String> {
    Template findByIdAndHiddenAndDeleted(String id, boolean hidden, boolean deleted);

    Template findByTitleAndDeleted(String title, boolean deleted);

    List<Template> findAllByHiddenAndDeleted(boolean hidden, boolean deleted);

}
