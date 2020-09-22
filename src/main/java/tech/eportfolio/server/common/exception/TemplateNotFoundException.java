package tech.eportfolio.server.common.exception;

public class TemplateNotFoundException extends RuntimeException {
    public static final String PROMPT = "template doesn't exist: ";

    public TemplateNotFoundException(Long id) {
        super(PROMPT + id);
    }

    public TemplateNotFoundException(String title) {
        super(PROMPT + title);
    }
}
