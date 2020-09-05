package tech.eportfolio.server.constant;

import static tech.eportfolio.server.constant.Authority.MODERATOR_AUTHORITIES;
import static tech.eportfolio.server.constant.Authority.USER_AUTHORITIES;

public enum Role {
    /**
     *
     */
    ROLE_VERIFIED_USER(USER_AUTHORITIES),
    ROLE_UNVERIFIED_USER(USER_AUTHORITIES),
    ROLE_MODERATOR(MODERATOR_AUTHORITIES);

    private final String[] authorities;

    Role(String... authorities) {
        this.authorities = authorities;
    }

    public String[] getAuthorities() {
        return authorities;
    }
}
