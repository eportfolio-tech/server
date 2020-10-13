package tech.eportfolio.server.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import tech.eportfolio.server.common.exception.TemplateNotFoundException;
import tech.eportfolio.server.common.exception.TitleAlreadyExistException;
import tech.eportfolio.server.common.exception.UserNotFoundException;
import tech.eportfolio.server.common.jsend.SuccessResponse;
import tech.eportfolio.server.dto.TemplateDTO;
import tech.eportfolio.server.model.Template;
import tech.eportfolio.server.model.User;
import tech.eportfolio.server.service.TemplateService;
import tech.eportfolio.server.service.UserService;

import java.util.List;

/**
 * @author Haswell
 */
@RestController
@RequestMapping("/templates")
public class TemplateController {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final TemplateService templateService;

    private final UserService userService;

    @Autowired
    public TemplateController(TemplateService templateService, @Qualifier("UserServiceCacheImpl") UserService userService) {
        this.templateService = templateService;
        this.userService = userService;
    }

    /**
     * Return all templates
     *
     * @return User
     */
    @GetMapping("/")
    @ApiOperation(value = "", authorizations = {@Authorization(value = "JWT")})
    public ResponseEntity<SuccessResponse<List<Template>>> findAllTemplates() {
        return new SuccessResponse<>("templates", templateService.findAvailableTemplates()).toOk();
    }

    /**
     * Return specific template by id
     *
     * @param templateId template id
     * @return Template
     */
    @GetMapping("/{templateId}")
    @ApiOperation(value = "", authorizations = {@Authorization(value = "JWT")})
    public ResponseEntity<SuccessResponse<Template>> findOneById(@PathVariable String templateId) {
        Template template = templateService.findTemplateById(templateId).orElseThrow(() -> new TemplateNotFoundException(templateId));
        return new SuccessResponse<>("template", template).toOk();
    }

    /**
     * Create a new template
     *
     * @return Template created template
     */
    @PostMapping("/")
    @ApiOperation(value = "", authorizations = {@Authorization(value = "JWT")})
    public ResponseEntity<SuccessResponse<Template>> createTemplate(@RequestBody TemplateDTO templateDTO) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByUsername(username).orElseThrow(() -> new UserNotFoundException(username));
        if (templateService.findTemplateByTitle(templateDTO.getTitle()).isPresent()) {
            throw new TitleAlreadyExistException(templateDTO.getTitle());
        }

        Template template = templateService.create(user, templateDTO);

        logger.debug("Template created {} by user {}", template.getId(), username);
        return new SuccessResponse<>("template", template).toCreated();
    }

    /**
     * Delete a template
     *
     * @return User
     */
    @DeleteMapping("/{templateId}")
    @ApiOperation(value = "", authorizations = {@Authorization(value = "JWT")})
    public ResponseEntity<SuccessResponse<Object>> deleteTemplate(@PathVariable String templateId) {
        Template template = templateService.findTemplateById(templateId).orElseThrow(() -> new TemplateNotFoundException(templateId));
        templateService.delete(template);
        return new SuccessResponse<>().toNoContent();
    }
}
