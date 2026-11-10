package com.roamandframe.checkout.api;

import com.roamandframe.checkout.exception.CheckoutInProgressException;
import com.roamandframe.checkout.exception.StepRejectedException;
import com.roamandframe.checkout.exception.StepUnavailableException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(StepRejectedException.class)
    ProblemDetail handleRejected(StepRejectedException ex) {
        HttpStatus status = HttpStatus.valueOf(ex.status());
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, ex.getMessage());
        problem.setTitle(status == HttpStatus.PAYMENT_REQUIRED ? "Payment declined" : "Checkout conflict");
        return problem;
    }

    @ExceptionHandler(CheckoutInProgressException.class)
    ProblemDetail handleInProgress(CheckoutInProgressException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problem.setTitle("Checkout already in progress");
        return problem;
    }

    @ExceptionHandler(StepUnavailableException.class)
    ProblemDetail handleUnavailable(StepUnavailableException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage());
        problem.setTitle("Checkout unavailable");
        return problem;
    }
}
