package tech.eportfolio.server.service;

import tech.eportfolio.server.model.Tag;

import java.util.List;
import java.util.Optional;

public interface TagService {
    Tag save(Tag tag);

    Optional<Tag> findById(long id);

    List<Tag> findAll();

    Tag createTag(String tagName);
}
