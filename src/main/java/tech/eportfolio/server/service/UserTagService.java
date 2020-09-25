package tech.eportfolio.server.service;

import tech.eportfolio.server.model.Tag;
import tech.eportfolio.server.model.User;
import tech.eportfolio.server.model.UserTag;

import java.util.List;

public interface UserTagService {
    List<UserTag> findByUsername(String username);

    List<Tag> findTagsByUser(User user);

    UserTag create(User user, Tag tag);

    List<UserTag> saveAll(List<UserTag> userTags);

    List<UserTag> batchAssign(User user, List<Tag> tags);

    List<UserTag> delete(User user, List<Tag> tag);
}
