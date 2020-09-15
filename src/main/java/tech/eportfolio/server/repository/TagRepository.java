package tech.eportfolio.server.repository;

import org.springframework.data.repository.CrudRepository;
import tech.eportfolio.server.model.Tag;

import java.util.List;

public interface TagRepository extends CrudRepository<Tag, Long> {
    Tag findByIdAndDeleted(long id, boolean deleted);

    Tag findFirstByNameAndDeleted(String name, boolean deleted);

    List<Tag> findByIdInAndDeleted(List<Long> id, boolean deleted);

    List<Tag> findByNameInAndDeleted(List<String> name, boolean deleted);

    List<Tag> findByDeleted(boolean deleted);
}
