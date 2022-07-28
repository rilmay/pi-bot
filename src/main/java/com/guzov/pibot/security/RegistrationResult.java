package com.guzov.pibot.security;

public enum RegistrationResult {
    ALREADY_REGISTERED("You are already registered"),
    SUCCESS("You are successfully registered"),
    FAILURE("Registration fail"),
    NOT_SUPPORTED("Operation is not supported");

    private String responseMessage;

    RegistrationResult(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public String getResponseMessage() {
        return responseMessage;
    }
}
