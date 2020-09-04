package tech.eportfolio.server.repository;

import org.springframework.data.repository.CrudRepository;
import tech.eportfolio.server.model.Tag;

import java.util.List;

public interface TagRepository extends CrudRepository<Tag, Long> {
    Tag findById(long id);

    Tag findFirstByName(String name);

    List<Tag> findByIdIn(List<Long> id);

    List<Tag> findByNameIn(List<String> name);
}
