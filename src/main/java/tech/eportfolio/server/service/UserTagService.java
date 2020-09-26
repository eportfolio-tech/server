package tech.eportfolio.server.service;

import tech.eportfolio.server.model.Tag;
import tech.eportfolio.server.model.User;
import tech.eportfolio.server.model.UserTag;

import javax.validation.constraints.NotNull;
import java.util.List;

public interface UserTagService {
    List<UserTag> findByUsername(String username);

    List<UserTag> findByTagId(String tagId);

    List<Tag> findTagsByUser(User user);

    List<User> findUsersByTag(@NotNull Tag tag);

    UserTag create(User user, Tag tag);

    List<UserTag> saveAll(List<UserTag> userTags);

    List<UserTag> batchAssign(User user, List<Tag> tags);

    List<UserTag> delete(User user, List<Tag> tag);
}
