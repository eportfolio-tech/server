package tech.eportfolio.server.service;

import tech.eportfolio.server.exception.TagNotFoundException;
import tech.eportfolio.server.model.Tag;

import java.util.List;

public interface TagService {
    Tag save(Tag tag);

    Tag findById(long id) throws TagNotFoundException;

    List<Tag> findAll();
}
