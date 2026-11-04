package com.roamandframe.gateway.exception;

public class CustomerUnavailableException extends RuntimeException {
    public CustomerUnavailableException(Throwable cause) { super("Customer service is not available", cause); }
}
