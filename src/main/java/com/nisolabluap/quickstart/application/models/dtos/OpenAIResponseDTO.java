package com.nisolabluap.quickstart.application.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OpenAIResponseDTO {

    private List<Choice> choices;

    @Data
    @AllArgsConstructor
    public static class Choice implements Serializable {

        private int index;
        private MessageDTO message;
    }
}
