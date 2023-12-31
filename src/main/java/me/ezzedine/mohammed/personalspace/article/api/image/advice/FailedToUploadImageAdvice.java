package me.ezzedine.mohammed.personalspace.article.api.image.advice;

import me.ezzedine.mohammed.personalspace.article.core.image.FailedToUploadImageException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class FailedToUploadImageAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(FailedToUploadImageException.class)
    protected ResponseEntity<Object> handle(FailedToUploadImageException exception, WebRequest request) {
        return handleExceptionInternal(exception, exception.getMessage(), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }
}