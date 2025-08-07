package com.jameswhatcott.finance.personal_finance_tracker.entity;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class Address {
    private String streetAddress;
    private String city;
    private String state;
    private String zipCode;
}
