package tech.eportfolio.server.repository;

import org.springframework.data.repository.CrudRepository;
import tech.eportfolio.server.model.Tag;

import java.util.List;

public interface TagRepository extends CrudRepository<Tag, Long> {
    List<Tag> findByName(String name);

    Tag findById(long id);
}
