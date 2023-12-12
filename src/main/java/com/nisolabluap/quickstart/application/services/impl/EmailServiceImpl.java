package com.nisolabluap.quickstart.application.services.impl;

import com.mailslurp.apis.InboxControllerApi;
import com.mailslurp.clients.ApiClient;
import com.mailslurp.clients.ApiException;
import com.mailslurp.clients.Configuration;
import com.mailslurp.models.SendEmailOptions;
import com.nisolabluap.quickstart.application.models.dtos.CustomerCreateDTO;
import com.nisolabluap.quickstart.application.services.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Collections;
import java.util.UUID;

@Service
@Slf4j
public class EmailServiceImpl implements EmailService {

    ApiClient defaultClient;
    UUID inboxId;
    @Autowired
    private TemplateEngine emailTemplateEngine;
    @Autowired
    public EmailServiceImpl(
            @Value("${mail.slurp.apiKey}") String mailSlurpApiKey,
            @Value("${mail.slurp.apiInbox}") String mailSlurpInboxId) {
        this.defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setApiKey(mailSlurpApiKey);
        this.inboxId = UUID.fromString(mailSlurpInboxId);
    }

    private void sendEmail(String subject, String body, String eMailAddress) {
        InboxControllerApi inboxControllerApi = new InboxControllerApi(defaultClient);
        try {
            SendEmailOptions sendEmailOptions = new SendEmailOptions()
                    .isHTML(true)
                    .to(Collections.singletonList(eMailAddress))
                    .subject(subject)
                    .body(body);
            inboxControllerApi.sendTestEmail(inboxId);
            inboxControllerApi.sendEmail(inboxId, sendEmailOptions);

        } catch (ApiException apiException) {
            log.info("Cannot send email to ".concat(eMailAddress));
            log.info(apiException.toString());
        }
    }

    @Override
    public void sendWelcomeEmail(CustomerCreateDTO customerCreateDTO) {
        Context context = new Context();
        context.setVariable("firstName", customerCreateDTO.getFirstName());
        context.setVariable("id", customerCreateDTO.getId());
        String emailContent = emailTemplateEngine.process("welcome", context);
        sendEmail("Welcome to RIM", emailContent, customerCreateDTO.getEmail());
    }
}
