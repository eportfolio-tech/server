package tech.eportfolio.server.common.constant;

/**
 * |                 | Anonymous | Unverified User | Verified User | Self |
 * | --------------- | --------- | --------------- | ------------- | ---- |
 * | PUBLIC          | 👍         | 👍               | 👍             | 👍    |
 * | UNVERIFIED_USER |           | 👍               | 👍             | 👍    |
 * | VERIFIED_USER   |           |                 | 👍             | 👍    |
 * | PRIVATE         |           |                 |               |      |
 */
public enum Visibility {
    PUBLIC,
    UNVERIFIED_USER,
    VERIFIED_USER,
    PRIVATE
}
