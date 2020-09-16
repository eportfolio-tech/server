package tech.eportfolio.server.common.exception;

public class PortfolioExistException extends RuntimeException {
    public PortfolioExistException(String message) {
        super("User " + message + " has already created an eportfolio");
    }
}
