package tech.eportfolio.server.common.exception;

public class SelfFollowCanNotBeAllowedException extends RuntimeException {
    public SelfFollowCanNotBeAllowedException(String username) {
        super("User " + username + " is following himself/herself which is not allowed");
    }
}
