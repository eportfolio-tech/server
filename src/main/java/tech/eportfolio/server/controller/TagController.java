package tech.eportfolio.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
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


}
