package com.priyanshu.deepresearchagent.exception;

public class ApiException extends RuntimeException {
    private final String serviceName;
    private final int statusCode;

    public ApiException(String message, String serviceName, int statusCode) {
        super(message);
        this.serviceName = serviceName;
        this.statusCode = statusCode;
    }

    public ApiException(String message, String serviceName, int statusCode, Throwable cause) {
        super(message, cause);
        this.serviceName = serviceName;
        this.statusCode = statusCode;
    }

    public String getServiceName() {
        return serviceName;
    }

    public int getStatusCode() {
        return statusCode;
    }
} 