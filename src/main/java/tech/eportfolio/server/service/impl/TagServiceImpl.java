package tech.eportfolio.server.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.eportfolio.server.exception.TagNotFoundException;
import tech.eportfolio.server.model.Tag;
import tech.eportfolio.server.repository.TagRepository;
import tech.eportfolio.server.service.TagService;

import java.util.List;

@Service
public class TagServiceImpl implements TagService {
    @Autowired
    private TagRepository tagRepository;

    @Override
    public Tag save(Tag tag) {
//        tag.setPassword(bCryptPasswordEncoder.encode(tag.getPassword()));
        return tagRepository.save(tag);
    }

    @Override
    public Tag findById(long id) throws TagNotFoundException {
        return tagRepository.findById(id);
    }

    @Override
    public List<Tag> findAll() {
        return (List<Tag>) tagRepository.findAll();
    }
}