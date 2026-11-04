package com.roamandframe.customer.api;

import com.roamandframe.customer.exception.CustomerNotFoundException;
import com.roamandframe.customer.exception.InvalidCredentialsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;

import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(InvalidCredentialsException.class)
    ProblemDetail handleInvalidCredentials(InvalidCredentialsException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(401), ex.getMessage());
        problem.setTitle("Invalid credentials");
        return problem;
    }

    @ExceptionHandler(CustomerNotFoundException.class)
    ProblemDetail handleCustomerNotFound(CustomerNotFoundException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(404), ex.getMessage());
        problem.setTitle("Customer not found");
        return problem;
    }
}
