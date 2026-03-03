package com.taxengine.exception;

public class RuleNotFoundException extends RuntimeException {

    public RuleNotFoundException(String municipalityCode, String serviceCode) {
        super(String.format(
                "No active ISS rule found for municipality '%s' and service code '%s'",
                municipalityCode, serviceCode));
    }
}
