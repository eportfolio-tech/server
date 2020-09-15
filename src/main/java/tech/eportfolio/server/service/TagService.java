package tech.eportfolio.server.service;

import tech.eportfolio.server.model.UserStorageContainer.Tag;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

public interface TagService {
    Optional<Tag> findById(long id);

    List<Tag> findAll();

    List<Tag> findByIdIn(List<Long> ids);

    List<Tag> findByNameIn(List<String> name);

    Tag save(Tag tag);

    Tag create(@NotEmpty @NotNull String name);

    List<Tag> saveAll(List<Tag> tags);

    List<Tag> saveAllIfNotExist(List<Tag> tags);

}
