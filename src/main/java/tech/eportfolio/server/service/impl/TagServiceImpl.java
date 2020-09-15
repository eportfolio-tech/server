package tech.eportfolio.server.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import tech.eportfolio.server.model.UserStorageContainer.Tag;
import tech.eportfolio.server.repository.TagRepository;
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

    @Override
    public Tag save(Tag tag) {
        return tagRepository.save(tag);
    }

    @Override
    public Optional<Tag> findById(long id) {
        return Optional.ofNullable(tagRepository.findByIdAndDeleted(id, false));
    }

    @Override
    public List<Tag> findAll() {
        return tagRepository.findByDeleted(false);
    }

    /**
     * Create a new tag if given tag is not found
     *
     * @param name tag name
     * @return Tag
     */
    @Override
    public Tag create(@NotEmpty @NotNull String name) {
        Tag tag = new Tag();
        tag.setName(name);
        tag.setCreatedBy(SecurityContextHolder.getContext().getAuthentication().getName());
        tagRepository.save(tag);
        return tag;
    }

    @Override
    public List<Tag> saveAll(List<Tag> tags) {
        Iterable<Tag> iterable = tagRepository.saveAll(tags);
        return StreamSupport.stream(iterable.spliterator(), false).collect(Collectors.toList());
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
    public List<Tag> findByIdIn(List<Long> ids) {
        return tagRepository.findByIdInAndDeleted(ids, false);
    }

    @Override
    public List<Tag> findByNameIn(List<String> name) {
        return tagRepository.findByNameInAndDeleted(name, false);
    }
}
