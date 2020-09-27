package tech.eportfolio.server.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import tech.eportfolio.server.model.UserTag;

import java.util.List;

@Repository
public interface UserTagRepository extends MongoRepository<UserTag, String> {
    List<UserTag> findByUsernameAndDeleted(String username, boolean deleted);

    List<UserTag> findByTagIdAndDeleted(String tagId, boolean deleted);

    List<UserTag> findBy(String tagName, boolean deleted);

    List<UserTag> findByTagIdInAndUsernameAndDeleted(List<String> tagIds, String username, Boolean delete);
}
