package tech.eportfolio.server.repository;

import org.springframework.data.repository.CrudRepository;
import tech.eportfolio.server.model.UserTag;

import java.util.List;

public interface UserTagRepository extends CrudRepository<UserTag, Long> {
    UserTag findById(long id);

    List<UserTag> findByUsername(String username);

    List<UserTag> findByUserID(Long userId);

    List<UserTag> findByTagId(Long tagId);
}
