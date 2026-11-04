package com.roamandframe.customer.model;

import java.util.UUID;

public record CustomerAccount(UUID id, String email, String passwordHash, CustomerRole role) {}
