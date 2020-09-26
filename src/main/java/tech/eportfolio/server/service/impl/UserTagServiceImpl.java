package tech.eportfolio.server.service.impl;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import tech.eportfolio.server.model.Tag;
import tech.eportfolio.server.model.User;
import tech.eportfolio.server.model.UserTag;
import tech.eportfolio.server.repository.UserTagRepository;
import tech.eportfolio.server.service.TagService;
import tech.eportfolio.server.service.UserService;
import tech.eportfolio.server.service.UserTagService;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@Qualifier("UserDetailsService")
public class UserTagServiceImpl implements UserTagService {

    private final UserTagRepository userTagRepository;
    private final TagService tagService;
    private final UserService userService;

    public UserTagServiceImpl(UserTagRepository userTagRepository, TagService tagService, UserService userService) {
        this.userTagRepository = userTagRepository;
        this.tagService = tagService;
        this.userService = userService;
    }

    @Override
    public List<UserTag> findByUsername(String username) {
        return userTagRepository.findByUsernameAndDeleted(username, false);
    }

    @Override
    public List<UserTag> findByTagId(String tagId) {
        return userTagRepository.findByTagIdAndDeleted(tagId, false);
    }

    @Override
    public List<Tag> findTagsByUser(@NotNull User user) {
        List<UserTag> userTags = findByUsername(user.getUsername());
        return tagService.findByIdIn(userTags.stream().map(UserTag::getTagId).collect(Collectors.toList()));
    }

    @Override
    public List<User> findUsersByTag(@NotNull Tag tag) {
        List<UserTag> userTags = findByTagId(tag.getId());
        return userService.findByIdIn(userTags.stream().map(UserTag::getUserId).collect(Collectors.toList()));
    }

    @Override
    public UserTag create(User user, Tag tag) {
        UserTag userTag = new UserTag();
        userTag.setTagId(tag.getId());
        userTag.setUserId(user.getId());
        userTag.setUsername(user.getUsername());
        return userTag;
    }

    @Override
    public List<UserTag> saveAll(List<UserTag> userTags) {
        return StreamSupport.stream(userTagRepository.saveAll(userTags).spliterator(), false).collect(Collectors.toList());
    }

    @Override
    public List<UserTag> batchAssign(@NotNull User user, @NotNull List<Tag> tags) {
        // Insert new tags to Tag table if not exist
        List<Tag> result = tagService.saveAllIfNotExist(tags);
        // Find user's existing tags
        List<UserTag> userTags = findByUsername(user.getUsername());
        List<String> currentTagIds = new ArrayList<>();
        userTags.forEach(e -> currentTagIds.add(e.getTagId()));

        List<UserTag> userTagsBatchInsert = new ArrayList<>();
        for (Tag t : result) {
            // if user doesn't have this tag assigned, then insert a new UserTag
            if (!currentTagIds.contains(t.getId())) {
                userTagsBatchInsert.add(this.create(user, t));
            }
        }
        return saveAll(userTagsBatchInsert);
    }

    @Override
    public List<UserTag> delete(User user, List<Tag> tag) {
        List<UserTag> userTags = userTagRepository.
                findByTagIdInAndUsernameAndDeleted(
                        tag.stream().map(Tag::getId).collect(Collectors.toList()),
                        user.getUsername(),
                        false);
        userTags.forEach(e -> e.setDeleted(true));
        userTagRepository.saveAll(userTags);
        return userTags;
    }


}