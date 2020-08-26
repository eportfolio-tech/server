package tech.eportfolio.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tech.eportfolio.server.exception.TagNotFoundException;
import tech.eportfolio.server.model.Tag;
import tech.eportfolio.server.repository.TagRepository;
import tech.eportfolio.server.service.TagService;

import java.util.List;

@RestController
public class TagController {
    @Autowired
    private TagService service;

    @Autowired
    private TagRepository repository;

    // Aggregate root

    @GetMapping("/tags")
    public List<Tag> findAll() {
        return service.findAll();
    }

    @PostMapping("/tags")
    public Tag createNewTag(@RequestBody Tag tag) {
        return service.save(tag);
    }

    // Single item

    @GetMapping("/tags/{id}")
    public Tag findOneTag(@PathVariable Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new TagNotFoundException(id));
    }

    @PutMapping("/tags/{id}")
    public Tag replaceTag(@RequestBody Tag newTag, @PathVariable Long id) {

        return repository.findById(id)
                .map(tag -> {
                    tag.setName(newTag.getName());
                    return repository.save(tag);
                })
                .orElseGet(() -> {
                    newTag.setId(id);
                    return repository.save(newTag);
                });
    }

    @DeleteMapping("/tags/{id}")
    public void deleteTag(@PathVariable Long id) {
        repository.deleteById(id);
    }


}
