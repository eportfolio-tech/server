package tech.eportfolio.server.common.exception;

public class PortfolioExistException extends RuntimeException {
    public PortfolioExistException(String message) {
        super("User already has already created an eportfolio" + message);
    }
}