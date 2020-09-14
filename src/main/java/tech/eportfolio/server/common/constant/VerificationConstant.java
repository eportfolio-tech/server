package tech.eportfolio.server.common.constant;

public class VerificationConstant {
    public static final String HOST = "dev.eportfolio.tech";
    public static final String SCHEME_HTTPS = "https";
    public static final String PATH = "/verification/verify";
    public static final String TOKEN = "token";
    public static final String USERNAME = "username";
    public static final String EMAIL_CONTENT = "\n" +
            "Hi %s,\n" +
            "\n" +
            "Thanks for registering an account on Forth-Two E-Portfolio.\n" +
            "\n" +
            "\n" +
            "Please click the following link to verify your email so we know it's you.\n" +
            "\n" +
            "%s\n" +
            "\n" +
            " \n" +
            "\n" +
            "Need help?\n" +
            "If you need help or have any concerns about why you're receiving this message please send email to admin@eportfolio.tech.\n" +
            "\n" +
            "If this email was not meant for you please let us know\n" +
            "\n" +
            "See you soon.\n" +
            "\n" +
            "Forth-Two E-Portfolio.";

    public static final String EMAIL_TITLE = "Forth-Two E-Portfolio Account Verification";
}
