package me.ezzedine.mohammed.personalspace.article.api.advice;

import me.ezzedine.mohammed.personalspace.article.core.highlight.ArticleAlreadyHighlightedException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ArticleAlreadyHighlightedAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ArticleAlreadyHighlightedException.class)
    protected ResponseEntity<Object> handle(ArticleAlreadyHighlightedException exception, WebRequest request) {
        return handleExceptionInternal(exception, exception.getMessage(), new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }
}
