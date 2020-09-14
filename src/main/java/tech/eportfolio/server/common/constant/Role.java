package tech.eportfolio.server.common.constant;

import static tech.eportfolio.server.common.constant.Authority.*;

public enum Role {
    /**
     *
     */
    ROLE_VERIFIED_USER(VERIFIED_USER_AUTHORITIES),
    ROLE_UNVERIFIED_USER(UNVERIFIED_USER_AUTHORITIES),
    ROLE_MODERATOR(MODERATOR_AUTHORITIES);

    private final String[] authorities;

    Role(String... authorities) {
        this.authorities = authorities;
    }

    public String[] getAuthorities() {
        return authorities;
    }
}
