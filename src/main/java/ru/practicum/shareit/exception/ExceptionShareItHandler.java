package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;

@RestControllerAdvice
public class ExceptionShareItHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseBody handleItemException(final Throwable e) {
        e.printStackTrace();
        return new ResponseBody(
                e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseBody handleItemNotFoundException(final ObjectNotFoundException e) {
        e.printStackTrace();
        return new ResponseBody(
                e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseBody handleBadRequestException(final BadRequestException  e) {
        e.printStackTrace();
        return new ResponseBody(
                e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseBody handleValidationException(final ConstraintViolationException e) {
        e.printStackTrace();
        return new ResponseBody(
                e.getMessage()
        );
    }
}