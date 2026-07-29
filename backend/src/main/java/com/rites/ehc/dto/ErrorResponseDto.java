package com.rites.ehc.dto;

import java.io.Serializable;

public class ErrorResponseDto implements Serializable {
    private boolean success = false;
    private int status;
    private String error;
    private String message;
    private String timestamp;

    public ErrorResponseDto() {
        this.timestamp = java.time.LocalDateTime.now().toString();
    }

    public ErrorResponseDto(int status, String error, String message) {
        this();
        this.status = status;
        this.error = error;
        this.message = message;
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    public String getError() { return error; }
    public void setError(String error) { this.error = error; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
}
