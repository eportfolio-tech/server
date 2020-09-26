package tech.eportfolio.server.service;


import tech.eportfolio.server.dto.TemplateDTO;
import tech.eportfolio.server.model.Template;
import tech.eportfolio.server.model.User;

import java.util.List;
import java.util.Optional;

public interface TemplateService {
    List<Template> findAvailableTemplates();

    Optional<Template> findTemplateById(String id);

    Template create(User user, TemplateDTO templateDTO);

    Template save(Template template);

    Template delete(Template template);

    Optional<Template> findTemplateByTitle(String title);
}
