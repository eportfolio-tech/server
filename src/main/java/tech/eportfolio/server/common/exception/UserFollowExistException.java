package tech.eportfolio.server.common.exception;

public class UserFollowExistException extends RuntimeException {
    public UserFollowExistException(String username, String followerName) {
        super("User " + username + " has already followed " + followerName);
    }
}
