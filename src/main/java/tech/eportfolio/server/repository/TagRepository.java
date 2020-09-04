package tech.eportfolio.server.repository;

import org.springframework.data.repository.CrudRepository;
import tech.eportfolio.server.model.Tag;

public interface TagRepository extends CrudRepository<Tag, Long> {
    Tag findById(long id);

    Tag findFirstByName(String name);
}
