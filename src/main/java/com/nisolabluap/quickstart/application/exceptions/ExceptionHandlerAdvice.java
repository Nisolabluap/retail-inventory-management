package com.nisolabluap.quickstart.application.exceptions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nisolabluap.quickstart.application.exceptions.customer.CustomerDataIntegrityException;
import com.nisolabluap.quickstart.application.exceptions.customer.CustomerNotFoundException;
import com.nisolabluap.quickstart.application.exceptions.customer.DuplicateEmailException;
import com.nisolabluap.quickstart.application.exceptions.item.*;
import com.nisolabluap.quickstart.application.exceptions.open_AI.OpenAIException;
import com.nisolabluap.quickstart.application.exceptions.order.InvalidQuantitiesException;
import com.nisolabluap.quickstart.application.exceptions.order.OrderAlreadyRefundedException;
import com.nisolabluap.quickstart.application.exceptions.order.OrderNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import static org.springframework.http.HttpStatus.*;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class ExceptionHandlerAdvice extends ResponseEntityExceptionHandler {

    private final ObjectMapper objectMapper;

    @ExceptionHandler(DuplicateItemException.class)
    public ResponseEntity<String> duplicateItemException(DuplicateItemException duplicateItemException) {
        return new ResponseEntity<>(objectToString(Map.of("message", duplicateItemException.getMessage())), BAD_REQUEST);
    }

    @ExceptionHandler(ItemNotFoundException.class)
    public ResponseEntity<String> itemNotFoundException(ItemNotFoundException inventoryNotFoundException) {
        return new ResponseEntity<>(objectToString(Map.of("message", inventoryNotFoundException.getMessage())), NOT_FOUND);
    }

    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<String> duplicateEmailException(DuplicateEmailException duplicateEmailException) {
        return new ResponseEntity<>(objectToString(Map.of("message", duplicateEmailException.getMessage())), BAD_REQUEST);
    }

    @ExceptionHandler(ItemsNotFoundException.class)
    public ResponseEntity<String> itemsNotFoundException(ItemsNotFoundException itemsNotFoundException) {
        return new ResponseEntity<>(objectToString(Map.of("message", itemsNotFoundException.getMessage())), NOT_FOUND);
    }

    @ExceptionHandler(CustomerNotFoundException.class)
    public ResponseEntity<String> customerNotFoundException(CustomerNotFoundException customerNotFoundException) {
        return new ResponseEntity<>(objectToString(Map.of("message", customerNotFoundException.getMessage())), NOT_FOUND);
    }

    @ExceptionHandler(ItemsOutOfStockException.class)
    public ResponseEntity<String> itemsOutOfStock(ItemsOutOfStockException itemsOutOfStockException) {
        return new ResponseEntity<>(objectToString(Map.of("message", itemsOutOfStockException.getMessage())), BAD_REQUEST);
    }

    @ExceptionHandler(InvalidQuantitiesException.class)
    public ResponseEntity<String> invalidQuantityException(InvalidQuantitiesException invalidQuantityException) {
        return new ResponseEntity<>(objectToString(Map.of("message", invalidQuantityException.getMessage())), BAD_REQUEST);
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<String> orderNotFoundException(OrderNotFoundException orderNotFoundException) {
        return new ResponseEntity<>(objectToString(Map.of("message", orderNotFoundException.getMessage())), NOT_FOUND);
    }

    @ExceptionHandler(OrderAlreadyRefundedException.class)
    public ResponseEntity<String> orderAlreadyRefundedException(OrderAlreadyRefundedException orderAlreadyRefundedException) {
        return new ResponseEntity<>(objectToString(Map.of("message", orderAlreadyRefundedException.getMessage())), BAD_REQUEST);
    }

    @ExceptionHandler(ItemDataIntegrityException.class)
    public ResponseEntity<String> itemDataIntegrityException(ItemDataIntegrityException itemDataIntegrityException) {
        return new ResponseEntity<>(objectToString(Map.of("message", itemDataIntegrityException.getMessage())), BAD_REQUEST);
    }

    @ExceptionHandler(CustomerDataIntegrityException.class)
    public ResponseEntity<String> customerDataIntegrityException(CustomerDataIntegrityException customerDataIntegrityException) {
        return new ResponseEntity<>(objectToString(Map.of("message", customerDataIntegrityException.getMessage())), BAD_REQUEST);
    }

    @ExceptionHandler(OpenAIException.class)
    public ResponseEntity<String> openAIException(OpenAIException openAIException) {
        return new ResponseEntity<>(objectToString(Map.of("message", openAIException.getMessage())), BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException ex, WebRequest request) {
        Map<String, String> errors = new LinkedHashMap<>();
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            String propertyPath = violation.getPropertyPath().toString();
            String fieldName = propertyPath.substring(propertyPath.lastIndexOf('.') + 1);
            String message = violation.getMessage();
            errors.put(fieldName, message);
        }
        return new ResponseEntity<>(objectToString(errors), BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        Map<String, String> errors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            String defaultMessage = Objects.requireNonNull(error.getDefaultMessage());
            errors.put(error.getField(), defaultMessage);
        });
        return new ResponseEntity<>(objectToString(errors), status);
    }

    private String objectToString(Object response) {
        try {
            return objectMapper.writeValueAsString(response);
        } catch (JsonProcessingException exception) {
            log.error("Error processing response to string");
            return "Internal error";
        }
    }
}
