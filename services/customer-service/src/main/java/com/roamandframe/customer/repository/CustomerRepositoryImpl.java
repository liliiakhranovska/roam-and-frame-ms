package com.roamandframe.customer.repository;

import com.roamandframe.customer.model.CustomerAccount;
import com.roamandframe.customer.model.CustomerProfile;
import com.roamandframe.customer.model.UpdateProfileCommand;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
class CustomerRepositoryImpl implements CustomerRepository {

    private final CustomerJpaRepository jpaRepository;

    CustomerRepositoryImpl(CustomerJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<CustomerAccount> findAccountByEmail(String email) {
        return jpaRepository.findByEmail(email)
                .map(e -> new CustomerAccount(e.getId(), e.getEmail(), e.getPasswordHash(), e.getRole()));
    }

    @Override
    public Optional<CustomerProfile> findProfileById(UUID id) {
        return jpaRepository.findById(id).map(this::toProfile);
    }

    @Override
    public Optional<CustomerProfile> updateProfile(UUID id, UpdateProfileCommand command) {
        return jpaRepository.findById(id).map(entity -> {
            entity.updateProfile(command);
            return toProfile(entity);
        });
    }

    private CustomerProfile toProfile(CustomerJpaEntity e) {
        return new CustomerProfile(
                e.getId(), e.getEmail(), e.getFirstName(), e.getLastName(),
                e.getPhone(), e.getAddressLine1(), e.getAddressLine2(),
                e.getCity(), e.getRegion(), e.getPostalCode(), e.getCountryCode());
    }
}

