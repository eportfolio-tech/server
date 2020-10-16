package tech.eportfolio.server.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import tech.eportfolio.server.common.constant.ActivityType;
import tech.eportfolio.server.common.constant.ParentType;
import tech.eportfolio.server.model.Activity;
import tech.eportfolio.server.model.Tag;
import tech.eportfolio.server.repository.TagRepository;
import tech.eportfolio.server.service.ActivityService;
import tech.eportfolio.server.service.TagService;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class TagServiceImpl implements TagService {
    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private ActivityService activityService;

    @Override
    public Tag save(Tag tag) {
        return tagRepository.save(tag);
    }

    @Override
    public Optional<Tag> findById(String id) {
        return Optional.ofNullable(tagRepository.findByIdAndDeleted(id, false));
    }

    @Override
    public Optional<Tag> findByName(String name) {
        return Optional.ofNullable(tagRepository.findByNameAndDeleted(name, false));
    }

    @Override
    public List<Tag> findAll() {
        return tagRepository.findByDeleted(false);
    }

    @Override
    public Tag create(@NotEmpty @NotNull String name) {
        Tag tag = Tag.builder().name(name).createdBy(SecurityContextHolder.getContext().getAuthentication().getName()).build();
        tag = tagRepository.save(tag);
        pushToActivity(tag);
        return tag;
    }

    @Override
    public Tag create(@NotEmpty @NotNull String name, String icon) {
        Tag tag = Tag.builder().name(name).createdBy(SecurityContextHolder.getContext().getAuthentication().getName()).icon(icon).build();
        tag = tagRepository.save(tag);
        pushToActivity(tag);
        return tag;
    }

    @Override
    public List<Tag> saveAll(List<Tag> tags) {
        Iterable<Tag> iterable = tagRepository.saveAll(tags);
        List<Tag> created = StreamSupport.stream(iterable.spliterator(), false).collect(Collectors.toList());
        // Add created tags to activities
        activityService.saveAll(created.stream().map(this::toTagActivity).collect(Collectors.toList()));
        return created;
    }

    @Override
    public List<Tag> saveAllIfNotExist(List<Tag> tags) {
        List<Tag> exist = findByNameIn(tags.stream().map(Tag::getName).collect(Collectors.toList()));
        Set<String> existTagNames = new HashSet<>();
        exist.forEach(e -> existTagNames.add(e.getName()));

        List<Tag> toCreate = new LinkedList<>();
        for (Tag t : tags) {
            if (!existTagNames.contains(t.getName())) {
                toCreate.add(create(t.getName()));
            }
        }
        exist.addAll(saveAll(toCreate));
        return exist;
    }

    @Override
    public Activity toTagActivity(Tag tag) {
        return Activity.builder()
                .activityType(ActivityType.TAG)
                .parentType(ParentType.TAG)
                .parentId(tag.getId())
                .username(tag.getCreatedBy())
                .deleted(false).build();
    }

    @Override
    public Activity pushToActivity(Tag tag) {
        // Create an new activity for the portfolio update
        Activity tagActivity = toTagActivity(tag);
        return activityService.save(tagActivity);
    }

    @Override
    public List<Activity> pushToActivity(List<Tag> tags) {
        // Create an new activity for the portfolio update
        return activityService.saveAll(tags.stream().map(this::toTagActivity).collect(Collectors.toList()));
    }

    @Override
    public List<Tag> findByIdIn(List<String> ids) {
        return tagRepository.findByIdInAndDeleted(ids, false);
    }

    @Override
    public List<Tag> findByNameIn(List<String> name) {
        return tagRepository.findByNameInAndDeleted(name, false);
    }
}
