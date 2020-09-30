package tech.eportfolio.server.common.exception;

public class UserFollowNotExistException extends RuntimeException {
    public UserFollowNotExistException(String username, String followerName) {
        super("User " + username + " has not followed " + followerName);
    }
}
