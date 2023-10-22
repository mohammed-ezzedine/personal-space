package me.ezzedine.mohammed.personalspace.category.api.advice;

import me.ezzedine.mohammed.personalspace.category.core.ArticleCategoryValidationViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ArticleCategoryValidationViolationAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ArticleCategoryValidationViolationException.class)
    protected ResponseEntity<Object> handle(ArticleCategoryValidationViolationException exception, WebRequest request) {
        CategoryCreationFailedApiModel response = CategoryCreationFailedApiModel.builder().failureReasons(exception.getReasons()).build();
        return handleExceptionInternal(exception, response, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }
}
