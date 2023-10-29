package me.ezzedine.mohammed.personalspace.article.api.advice;

import me.ezzedine.mohammed.personalspace.article.core.ArticleNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ArticleNotFoundAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ArticleNotFoundException.class)
    protected ResponseEntity<Object> handle(ArticleNotFoundException exception, WebRequest request) {
        return handleExceptionInternal(exception, exception.getMessage(), new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }
}
