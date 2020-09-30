package tech.eportfolio.server.common.exception;

public class SelfUnfollowCanNotBeAllowedException extends RuntimeException {
    public SelfUnfollowCanNotBeAllowedException(String username) {
        super("User " + username + " is unfollowing himself/herself which is not allowed");
    }
}
