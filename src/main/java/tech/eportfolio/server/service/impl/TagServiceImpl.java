package tech.eportfolio.server.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import tech.eportfolio.server.model.Tag;
import tech.eportfolio.server.repository.TagRepository;
import tech.eportfolio.server.service.TagService;

import java.util.List;
import java.util.Optional;

@Service
public class TagServiceImpl implements TagService {
    @Autowired
    private TagRepository tagRepository;

    @Override
    public Tag save(Tag tag) {
        return tagRepository.save(tag);
    }

    @Override
    public Optional<Tag> findById(long id) {
        return Optional.ofNullable(tagRepository.findById(id));
    }

    @Override
    public List<Tag> findAll() {
        return (List<Tag>) tagRepository.findAll();
    }

    /**
     * Create a new tag if given tag is not found
     *
     * @param name
     * @return
     */
    @Override
    public Tag createTag(String name) {
        Tag tag = tagRepository.findFirstByName(name);
        if (tag != null) {
            return tag;
        }
        tag = new Tag();
        tag.setName(name);
        tag.setCreatedBy(SecurityContextHolder.getContext().getAuthentication().getName());
        tagRepository.save(tag);
        return tag;
    }
}
