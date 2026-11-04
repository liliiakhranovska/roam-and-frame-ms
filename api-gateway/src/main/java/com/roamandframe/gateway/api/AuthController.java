package com.roamandframe.gateway.api;

import com.roamandframe.gateway.client.AuthenticatedCustomer;
import com.roamandframe.gateway.client.CustomerClient;
import com.roamandframe.gateway.security.TokenIssuer;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
class AuthController {

    private final CustomerClient customerClient;
    private final TokenIssuer tokenIssuer;

    AuthController(CustomerClient customerClient, TokenIssuer tokenIssuer) {
        this.customerClient = customerClient;
        this.tokenIssuer = tokenIssuer;
    }

    @PostMapping("/token")
    AuthenticationResponse token(@Valid @RequestBody AuthenticationRequest request) {
        AuthenticatedCustomer customer = customerClient.authenticate(request.email(), request.password());
        return new AuthenticationResponse(tokenIssuer.issueToken(customer), "Bearer");
    }
}
