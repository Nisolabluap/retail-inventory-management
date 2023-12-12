package com.nisolabluap.quickstart.application.services;

import com.nisolabluap.quickstart.application.models.dtos.CustomerCreateDTO;
import org.springframework.stereotype.Service;

@Service
public interface EmailService {

    void sendWelcomeEmail(CustomerCreateDTO customerCreateDTO);
}
