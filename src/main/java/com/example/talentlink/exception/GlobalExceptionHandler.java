package com.example.talentlink.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = BusinessRuleException.class, produces = MediaType.TEXT_HTML_VALUE)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public String handleBusinessRule() {
        return "error/422";
    }

    @ExceptionHandler(value = {ResourceNotFoundException.class, NoHandlerFoundException.class,
            NoResourceFoundException.class}, produces = MediaType.TEXT_HTML_VALUE)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFound() {
        return "error/404";
    }

    @ExceptionHandler(value = AccessDeniedException.class, produces = MediaType.TEXT_HTML_VALUE)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String handleAccessDenied() {
        return "error/403";
    }
}
