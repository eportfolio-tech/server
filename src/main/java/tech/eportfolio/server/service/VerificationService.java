package tech.eportfolio.server.service;

import tech.eportfolio.server.model.User;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public interface VerificationService {
    User verify(@NotNull User user, @NotEmpty String token);

    String generateVerificationToken(@NotNull User user);

    String buildLink(@NotNull User user, String token);

    void sendVerificationEmail(@NotNull User user);


}
