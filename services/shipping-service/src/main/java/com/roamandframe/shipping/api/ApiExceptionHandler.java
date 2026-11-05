package com.roamandframe.shipping.api;

import com.roamandframe.shipping.exception.ShipmentAlreadyShippedException;
import com.roamandframe.shipping.exception.ShipmentCancelledException;
import com.roamandframe.shipping.exception.ShipmentNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({ShipmentCancelledException.class, ShipmentAlreadyShippedException.class})
    ProblemDetail handleConflict(RuntimeException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problem.setTitle("Shipment conflict");
        return problem;
    }

    @ExceptionHandler(ShipmentNotFoundException.class)
    ProblemDetail handleNotFound(ShipmentNotFoundException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("Shipment not found");
        return problem;
    }
}
