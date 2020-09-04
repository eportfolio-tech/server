package tech.eportfolio.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tech.eportfolio.server.exception.TagNotFoundException;
import tech.eportfolio.server.model.Tag;
import tech.eportfolio.server.repository.TagRepository;
import tech.eportfolio.server.service.TagService;

import java.util.List;

@RestController
@RequestMapping("/tags")

public class TagController {
    @Autowired
    private TagService service;

    @Autowired
    private TagRepository repository;

    // Aggregate root
    @GetMapping("/")
    public List<Tag> findAll() {
        return service.findAll();
    }

    @PostMapping("/")
    public Tag createNewTag(@RequestParam String name) {
        return service.create(name);
    }

    // Single item
    @GetMapping("/{id}")
    public Tag findOneTag(@PathVariable Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new TagNotFoundException(id));
    }
}
