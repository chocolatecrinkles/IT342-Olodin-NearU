package edu.cit.olodin.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ErrorResponse> handleAuth(AuthException ex) {

        HttpStatus status;

        switch (ex.getErrorCode()) {

            case "AUTH_UNAUTHORIZED":
                status = HttpStatus.FORBIDDEN;
                break;

            case "AUTH_USER_NOT_FOUND":
            case "LISTING_NOT_FOUND":
            case "BOOKMARK_NOT_FOUND":
                status = HttpStatus.NOT_FOUND;
                break;

            case "AUTH_INVALID_PASSWORD":
            case "AUTH_EMAIL_EXISTS":
            case "BOOKMARK_EXISTS":
            case "VALIDATION_ERROR":
                status = HttpStatus.BAD_REQUEST;
                break;

            default:
                status = HttpStatus.BAD_REQUEST;
        }

        return ResponseEntity
                .status(status)
                .body(new ErrorResponse(ex.getMessage(), ex.getErrorCode()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        return ResponseEntity
                .status(500)
                .body(new ErrorResponse("Something went wrong", "INTERNAL_ERROR"));
    }
}