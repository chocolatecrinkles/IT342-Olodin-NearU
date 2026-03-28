package edu.cit.olodin.dto;

public class ErrorResponse {
    private boolean success;
    private String message;
    private String error;

    public ErrorResponse(String message, String error) {
        this.success = false;
        this.message = message;
        this.error = error;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public String getError() {
        return error;
    }
}
