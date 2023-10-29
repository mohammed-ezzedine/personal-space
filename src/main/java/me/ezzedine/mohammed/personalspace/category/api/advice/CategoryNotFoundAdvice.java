package me.ezzedine.mohammed.personalspace.category.api.advice;

import me.ezzedine.mohammed.personalspace.category.core.CategoryNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class CategoryNotFoundAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(CategoryNotFoundException.class)
    protected ResponseEntity<Object> handle(CategoryNotFoundException exception, WebRequest request) {
        return handleExceptionInternal(exception, exception.getMessage(), new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }
}