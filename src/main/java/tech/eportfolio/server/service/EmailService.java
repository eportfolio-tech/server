package tech.eportfolio.server.service;

import org.springframework.scheduling.annotation.Async;

public interface EmailService {
    @Async
    void sendSimpleMessage(String to, String subject, String text);
}
