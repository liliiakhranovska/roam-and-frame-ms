package com.roamandframe.order.api;

import com.roamandframe.order.exception.OrderAlreadyShippedException;
import com.roamandframe.order.exception.OrderCancelledException;
import com.roamandframe.order.exception.OrderNotFoundException;
import com.roamandframe.order.exception.ShipmentRejectedException;
import com.roamandframe.order.exception.ShippingUnavailableException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(OrderNotFoundException.class)
    ProblemDetail handleNotFound(OrderNotFoundException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("Order not found");
        return problem;
    }

    @ExceptionHandler({OrderCancelledException.class, OrderAlreadyShippedException.class,
            ShipmentRejectedException.class})
    ProblemDetail handleConflict(RuntimeException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problem.setTitle("Order conflict");
        return problem;
    }

    @ExceptionHandler(ShippingUnavailableException.class)
    ProblemDetail handleShippingUnavailable(ShippingUnavailableException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage());
        problem.setTitle("Shipment information unavailable");
        return problem;
    }
}
