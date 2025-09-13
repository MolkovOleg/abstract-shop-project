package com.molkovor.catalogue.controller.advice;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Locale;

@ControllerAdvice
@RequiredArgsConstructor
public class BadRequestControllerAdvice {

    private final MessageSource messageSource;

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ProblemDetail> handleBindException(BindException ex, Locale locale) {
        ProblemDetail problemDetail = ProblemDetail
                .forStatusAndDetail(HttpStatus.BAD_REQUEST, messageSource.getMessage("catalogue.errors.404.title",
                        new Object[0], "catalogue.errors.404.title", locale));
        problemDetail.setProperty("errors", ex.getAllErrors().stream()
                .map(MessageSourceResolvable::getDefaultMessage)
                .toList());

        return ResponseEntity
                .badRequest()
                .body(problemDetail);

    }
}
