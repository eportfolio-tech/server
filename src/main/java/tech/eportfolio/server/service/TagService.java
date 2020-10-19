package tech.eportfolio.server.service;

import tech.eportfolio.server.model.Activity;
import tech.eportfolio.server.model.Tag;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

public interface TagService {
    Optional<Tag> findById(String id);

    Optional<Tag> findByName(String name);

    List<Tag> findAll();

    List<Tag> findByIdIn(List<String> ids);

    List<Tag> findByNameIn(List<String> name);

    Tag save(Tag tag);

    Tag create(@NotEmpty @NotNull String name);

    List<Tag> saveAll(List<Tag> tags);

    List<Tag> saveAllIfNotExist(List<Tag> tags);

    Activity toTagActivity(Tag tag);

    Activity pushToActivity(Tag tag);

    List<Activity> pushToActivity(List<Tag> tags);

    Tag create(@NotEmpty @NotNull String name, String icon);
}
