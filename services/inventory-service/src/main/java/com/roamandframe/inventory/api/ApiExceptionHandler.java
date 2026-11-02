package com.roamandframe.inventory.api;

import com.roamandframe.inventory.exception.ReservationReleasedException;
import com.roamandframe.inventory.exception.StockReservationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({StockReservationException.class, ReservationReleasedException.class})
    ProblemDetail handleConflict(RuntimeException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problem.setTitle("Stock reservation not possible");
        return problem;
    }
}
