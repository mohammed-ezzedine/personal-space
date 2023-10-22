package me.ezzedine.mohammed.personalspace.category.api.advice;

import me.ezzedine.mohammed.personalspace.category.core.ArticleCategoryIdAlreadyExistsException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Collections;

@ControllerAdvice
public class ArticleCategoryIdAlreadyExistsAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ArticleCategoryIdAlreadyExistsException.class)
    protected ResponseEntity<Object> handle(ArticleCategoryIdAlreadyExistsException exception, WebRequest request) {
        CategoryCreationFailedApiModel response = CategoryCreationFailedApiModel.builder().failureReasons(Collections.singletonList(exception.getMessage())).build();
        return handleExceptionInternal(exception, response, new HttpHeaders(), HttpStatus.CONFLICT, request);
    }
}
