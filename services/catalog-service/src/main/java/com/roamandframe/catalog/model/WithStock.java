package com.roamandframe.catalog.model;

public record WithStock<T>(T item, int quantity) {
}
