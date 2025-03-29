package com.elbialy.book.auth;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Async
    public void sendEmail(
            String to,
            String username,
            EmailTemplateName emailTemplate,
            String confirmationUrl,
            String activationCode,
            String subject
    ) throws MessagingException {
        String templateName;
        if (emailTemplate != null) {
            templateName = emailTemplate.name();
        }
        else {
            templateName = "confirm_email";
        }
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message , true, "UTF-8");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setFrom("elbialy@elbialy.com");

        Map<String, Object> model = new HashMap<>();
        model.put("confirmationUrl",confirmationUrl);
        model.put("activation_code", activationCode);
        model.put("username", username);
        Context context = new Context();
        context.setVariables(model);

        String template = templateEngine.process(templateName, context);
        helper.setText(template, true);

        mailSender.send(message);

    }


}
