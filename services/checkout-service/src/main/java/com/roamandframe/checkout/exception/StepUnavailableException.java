package com.roamandframe.checkout.exception;

public class StepUnavailableException extends RuntimeException {

    public StepUnavailableException(String service, Throwable cause) {
        super(service + " service is unavailable", cause);
    }
}
