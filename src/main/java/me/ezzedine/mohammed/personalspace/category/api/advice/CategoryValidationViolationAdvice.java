package me.ezzedine.mohammed.personalspace.category.api.advice;

import me.ezzedine.mohammed.personalspace.category.core.CategoryValidationViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class CategoryValidationViolationAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(CategoryValidationViolationException.class)
    protected ResponseEntity<Object> handle(CategoryValidationViolationException exception, WebRequest request) {
        CategoryCreationFailedApiModel response = CategoryCreationFailedApiModel.builder().failureReasons(exception.getReasons()).build();
        return handleExceptionInternal(exception, response, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }
}
