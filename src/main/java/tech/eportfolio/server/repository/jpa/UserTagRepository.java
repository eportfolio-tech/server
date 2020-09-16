package tech.eportfolio.server.repository.jpa;

import org.springframework.data.repository.CrudRepository;
import tech.eportfolio.server.model.UserTag;

import java.util.List;

public interface UserTagRepository extends CrudRepository<UserTag, Long> {
    UserTag findById(long id);

    List<UserTag> findByUsernameAndDeleted(String username, boolean deleted);

    List<UserTag> findByUserIdAndDeleted(Long userId, boolean deleted);

    List<UserTag> findByTagIdAndDeleted(Long tagId, boolean deleted);

    List<UserTag> findByTagIdInAndUsernameAndDeleted(List<Long> tagIds, String username, Boolean delete);
}
