package com.roamandframe.customer.api;

import com.roamandframe.customer.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/internal/customers")
class CustomerController {

    private final CustomerService customerService;

    CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping("/authentication")
    AuthenticatedCustomerResponse authenticate(@Valid @RequestBody AuthenticationRequest request) {
        return AuthenticatedCustomerResponse.from(
                customerService.authenticate(request.email(), request.password()));
    }

    @GetMapping("/{id}")
    ProfileResponse getProfile(@PathVariable UUID id) {
        return ProfileResponse.from(customerService.getProfile(id));
    }
}