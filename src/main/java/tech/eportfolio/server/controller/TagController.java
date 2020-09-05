package tech.eportfolio.server.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tech.eportfolio.server.exception.TagNotFoundException;
import tech.eportfolio.server.model.Tag;
import tech.eportfolio.server.service.TagService;

import java.util.List;

@RestController
@RequestMapping("/tags")
public class TagController {
    @Autowired
    private TagService tagService;

    // Aggregate root
    @GetMapping("/")
    public List<Tag> findAll() {
        return tagService.findAll();
    }

    @PostMapping("/")
    @ApiOperation(value = "", authorizations = {@Authorization(value = "JWT")})
    public Tag createNewTag(@RequestParam String name) {
        return tagService.create(name);
    }

    // Single item
    @GetMapping("/{id}")
    @ApiOperation(value = "", authorizations = {@Authorization(value = "JWT")})
    public Tag findOneTag(@PathVariable Long id) {
        return tagService.findById(id)
                .orElseThrow(() -> new TagNotFoundException(id));
    }
}
