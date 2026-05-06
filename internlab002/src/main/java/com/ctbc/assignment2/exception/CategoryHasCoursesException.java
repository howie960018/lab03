package com.ctbc.assignment2.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CategoryHasCoursesException extends RuntimeException {
    public CategoryHasCoursesException(String message) {
        super(message);
    }
}