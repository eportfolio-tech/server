package tech.eportfolio.server.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import tech.eportfolio.server.service.EmailService;

@Component
public class EmailServiceImpl implements EmailService {

    private final Logger Logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private JavaMailSender emailSender;

    /**
     * This method is annotated with @Async to send email asynchronously
     * See https://stackoverflow.com/questions/58009982/how-to-send-an-asynchrone-email-with-spring-boot
     *
     * @param to
     * @param subject
     * @param text
     */
    @Override
    @Async
    public void sendSimpleMessage(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("no-reply@eportfolio.tech");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        try {
            emailSender.send(message);
        } catch (MailException exception) {
            Logger.error("Unable to send email to {}{}", exception, to);
        }
    }
}