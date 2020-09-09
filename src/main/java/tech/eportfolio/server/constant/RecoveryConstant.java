package tech.eportfolio.server.constant;

public class RecoveryConstant {
    public static final String HOST = "dev.eportfolio.tech";
    public static final String SCHEME_HTTPS = "https";
    public static final String PATH = "/authentication/password-recovery";
    public static final String TOKEN = "token";
    public static final String USERNAME = "username";
    public static final String EMAIL_CONTENT = "\n" +
            "Hi %s,\n" +
            "\n" +
            "You recently requested to reset your password for your account.\n" +
            "\n" +
            "\n" +
            "Please click the following link to verify your email so we know it's you.\n" +
            "\n" +
            "%s\n" +
            "\n" +
            "For security reasons, this link will expire in 30 minutes from original datetime of this request.\n" +
            " \n" +
            "\n" +
            "Need help?\n" +
            "If you did not request a password reset, please ignore this email or send email to admin@eportfolio.tech to let us know.\n" +
            "\n" +
            "Thanks,\n" +
            "Forth-Two E-Portfolio.";

    public static final String EMAIL_TITLE = "Forth-Two E-Portfolio Password Recovery";
}
