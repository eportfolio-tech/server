package tech.eportfolio.server.service;

import tech.eportfolio.server.model.User;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public interface RecoveryService {

    User passwordRecovery(@NotNull User user, @NotEmpty String token, String newPassword);

    String buildRecoveryLink(@NotNull User user, String token);

    void sendRecoveryEmail(@NotNull User user);

    String generatePasswordRecoveryToken(@NotNull User user);

    String getPasswordRecoverySecret(@NotNull User user);

}
