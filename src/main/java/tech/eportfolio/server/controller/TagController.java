package tech.eportfolio.server.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.eportfolio.server.common.exception.TagNotFoundException;
import tech.eportfolio.server.common.jsend.SuccessResponse;
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
    public ResponseEntity<SuccessResponse<List<Tag>>> findAll() {
        return new SuccessResponse<>("tag", tagService.findAll()).toOk();
    }

    @PostMapping("/")
    @ApiOperation(value = "", authorizations = {@Authorization(value = "JWT")})
    public ResponseEntity<SuccessResponse<Tag>> createNewTag(@RequestParam String name) {
        return new SuccessResponse<>("tag", tagService.create(name)).toOk();

    }

    // Single item
    @GetMapping("/{id}")
    @ApiOperation(value = "", authorizations = {@Authorization(value = "JWT")})
    public ResponseEntity<SuccessResponse<Tag>> findOneTag(@PathVariable Long id) {
        Tag tag = tagService.findById(id)
                .orElseThrow(() -> new TagNotFoundException(id));
        return new SuccessResponse<>("tag", tag).toOk();
    }
}
