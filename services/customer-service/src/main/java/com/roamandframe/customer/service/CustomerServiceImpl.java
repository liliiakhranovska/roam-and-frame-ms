package com.roamandframe.customer.service;

import com.roamandframe.customer.exception.CustomerNotFoundException;
import com.roamandframe.customer.exception.InvalidCredentialsException;
import com.roamandframe.customer.model.AuthenticatedCustomer;
import com.roamandframe.customer.model.CustomerAccount;
import com.roamandframe.customer.model.CustomerProfile;
import com.roamandframe.customer.model.UpdateProfileCommand;
import com.roamandframe.customer.repository.CustomerRepository;
import com.roamandframe.customer.security.PasswordHasher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final PasswordHasher passwordHasher;

    public CustomerServiceImpl(CustomerRepository customerRepository, PasswordHasher passwordHasher) {
        this.customerRepository = customerRepository;
        this.passwordHasher = passwordHasher;
    }

    @Transactional(readOnly = true)
    @Override
    public AuthenticatedCustomer authenticate(String email, String rawPassword) {
        CustomerAccount account = customerRepository.findAccountByEmail(email)
                .orElseThrow(InvalidCredentialsException::new);
        if (!passwordHasher.matches(rawPassword, account.passwordHash())) {
            throw new InvalidCredentialsException();
        }
        return new AuthenticatedCustomer(account.id(), account.email(), account.role());
    }

    @Transactional(readOnly = true)
    @Override
    public CustomerProfile getProfile(UUID customerId) {
        return customerRepository.findProfileById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(customerId));
    }

    @Transactional
    @Override
    public CustomerProfile updateProfile(UUID customerId, UpdateProfileCommand command) {
        return customerRepository.updateProfile(customerId, command)
                .orElseThrow(() -> new CustomerNotFoundException(customerId));
    }
}
