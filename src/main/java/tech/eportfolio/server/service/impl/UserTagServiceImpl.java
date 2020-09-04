package tech.eportfolio.server.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private final UserTagRepository userTagRepository;
    private final TagService tagService;
    private UserService userService;

    public UserTagServiceImpl(UserTagRepository userTagRepository, TagService tagService) {
        this.userTagRepository = userTagRepository;
        this.tagService = tagService;
    }

    @Override
    public List<UserTag> findByUsername(String username) {
        return userTagRepository.findByUsername(username);
    }

    @Override
    public List<UserTag> findByUserID(Long userID) {
        return userTagRepository.findByUserID(userID);
    }

    @Override
    public List<Tag> findTagsByUsername(String username) {
        List<UserTag> userTags = findByUsername(username);
        return tagService.findByIdIn(userTags.stream().map(UserTag::getTagId).collect(Collectors.toList()));
    }

    @Override
    public UserTag create(User user, Tag tag) {
        UserTag userTag = new UserTag();
        userTag.setTagId(tag.getId());
        userTag.setUserID(user.getId());
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
        List<Long> currentTagIds = new ArrayList<>();
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
}