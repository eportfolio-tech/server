package tech.eportfolio.server.service;


import tech.eportfolio.server.model.Template;

import java.util.List;
import java.util.Optional;

public interface TemplateService {
    List<Template> findAvailableTemplates();

    Optional<Template> findTemplateById(String id);

    Template save(Template template);

    Template delete(Template template);

    Optional<Template> findTemplateByTitle(String title);
}
