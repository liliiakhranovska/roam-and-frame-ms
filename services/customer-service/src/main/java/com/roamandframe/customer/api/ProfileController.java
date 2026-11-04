package com.roamandframe.customer.api;

import com.roamandframe.customer.model.UpdateProfileCommand;
import com.roamandframe.customer.security.CurrentCustomerId;
import com.roamandframe.customer.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customer/profile")
class ProfileController {

    private final CustomerService customerService;

    ProfileController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    ProfileResponse getProfile(@CurrentCustomerId UUID customerId) {
        return ProfileResponse.from(customerService.getProfile(customerId));
    }

    @PutMapping
    ProfileResponse updateProfile(@CurrentCustomerId UUID customerId,
                                  @Valid @RequestBody UpdateProfileRequest request) {
        UpdateProfileCommand command = new UpdateProfileCommand(
                request.firstName(), request.lastName(), request.phone(),
                request.addressLine1(), request.addressLine2(), request.city(),
                request.region(), request.postalCode(), request.countryCode());
        return ProfileResponse.from(customerService.updateProfile(customerId, command));
    }
}
