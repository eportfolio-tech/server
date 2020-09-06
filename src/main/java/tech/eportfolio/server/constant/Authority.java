package tech.eportfolio.server.constant;

public class Authority {
    public static final String[] UNVERIFIED_USER_AUTHORITIES = {"user:read", "user:create"};
    public static final String[] VERIFIED_USER_AUTHORITIES = {"user:read", "user:create", "user:update"};
    public static final String[] MODERATOR_AUTHORITIES = {"user:read", "user:create", "user:update", "user:delete"};
}
