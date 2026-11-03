package com.roamandframe.payment.api;

import com.roamandframe.payment.exception.PaymentDeclinedException;
import com.roamandframe.payment.exception.PaymentVoidedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;

import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(PaymentDeclinedException.class)
    ProblemDetail handleDeclined(PaymentDeclinedException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.PAYMENT_REQUIRED, ex.getMessage());
        problem.setTitle("Payment declined");
        return problem;
    }

    @ExceptionHandler(PaymentVoidedException.class)
    ProblemDetail handleVoided(PaymentVoidedException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problem.setTitle("Payment already voided");
        return problem;
    }
}
