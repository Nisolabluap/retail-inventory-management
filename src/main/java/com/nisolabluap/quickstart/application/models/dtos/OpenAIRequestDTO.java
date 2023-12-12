package com.nisolabluap.quickstart.application.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OpenAIRequestDTO {

    private String model;
    private List<MessageDTO> messages;

    public OpenAIRequestDTO(String model, String prompt) {
        this.model = model;
        this.messages = new ArrayList<>();
        this.messages.add(new MessageDTO("user", prompt));
    }
}
