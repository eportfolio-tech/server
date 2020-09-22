package tech.eportfolio.server.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.eportfolio.server.model.Template;
import tech.eportfolio.server.repository.mongodb.TemplateRepository;
import tech.eportfolio.server.service.TemplateService;

import java.util.List;
import java.util.Optional;

@Service
public class TemplateServiceImpl implements TemplateService {
    @Autowired
    private TemplateRepository templateRepository;

    @Override
    public List<Template> findAvailableTemplates() {
        return templateRepository.findAllByHiddenAndDeleted(false, false);
    }

    @Override
    public Optional<Template> findTemplateById(String id) {
        return Optional.ofNullable(templateRepository.findByIdAndHiddenAndDeleted(id, false, false));
    }

    @Override
    public Template save(Template template) {
        return templateRepository.save(template);
    }

    @Override
    public Template delete(Template template) {
        template.setDeleted(true);
        return templateRepository.save(template);
    }

    @Override
    public Optional<Template> findTemplateByTitle(String title) {
        return Optional.ofNullable(templateRepository.findByTitleAndDeleted(title, false));
    }
}
