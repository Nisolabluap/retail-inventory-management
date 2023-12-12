package com.nisolabluap.quickstart.application.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nisolabluap.quickstart.application.exceptions.open_AI.OpenAIException;
import com.nisolabluap.quickstart.application.models.dtos.OpenAIRequestDTO;
import com.nisolabluap.quickstart.application.models.dtos.OpenAIResponseDTO;
import com.nisolabluap.quickstart.application.services.OpenAIIntegrationService;
import com.nisolabluap.quickstart.application.services.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Service
public class OpenAIIntegrationServiceImpl implements OpenAIIntegrationService {

    @Autowired
    private final Environment environment;
    @Autowired
    private final ObjectMapper objectMapper;
    @Autowired
    private final Validator validator;

    @Override
    public String getOpenAIResponse(String prompt) {
        String jsonBody = null;
        try {
            jsonBody = objectMapper.writeValueAsString(new OpenAIRequestDTO(environment.getProperty("open.ai.model"), prompt));
        } catch (JsonProcessingException e) {
            throw new OpenAIException(validator.getOpenAIMessage());
        }

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonBody);
        Request request = new Request.Builder()
                .url(Objects.requireNonNull(environment.getProperty("open.ai.url")))
                .header("Authorization", "Bearer " + environment.getProperty("open.ai.key"))
                .post(requestBody)
                .build();
        try (Response response = new OkHttpClient().newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                String responseBody = response.body().string();
                return objectMapper.readValue(responseBody, OpenAIResponseDTO.class).getChoices().get(0).getMessage().getContent();
            }
            throw new OpenAIException(validator.getOpenAIMessage());
        } catch (IOException ex) {
            throw new OpenAIException(validator.getOpenAIMessage());
        }
    }
}
