package tech.eportfolio.server.common.exception;

public class PortfolioNotFoundException extends RuntimeException {
    public static final String PROMPT = "portfolio not found: ";

    public PortfolioNotFoundException(Long id) {
        super(PROMPT + id);
    }

    public PortfolioNotFoundException(String username) {
        super(PROMPT + username);
    }
}
