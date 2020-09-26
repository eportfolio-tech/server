package tech.eportfolio.server.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import tech.eportfolio.server.model.Tag;

import java.util.List;

@Repository
public interface TagRepository extends MongoRepository<Tag, String> {
    Tag findByIdAndDeleted(String id, boolean deleted);

    List<Tag> findByIdInAndDeleted(List<String> id, boolean deleted);

    List<Tag> findByNameInAndDeleted(List<String> name, boolean deleted);

    List<Tag> findByDeleted(boolean deleted);
}
