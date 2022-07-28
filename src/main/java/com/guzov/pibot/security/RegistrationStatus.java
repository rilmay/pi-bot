package com.guzov.pibot.security;

public enum RegistrationStatus {
    REGISTERED("registered"),
    NOT_REGISTERED("not registered");

    private final String statusName;

    RegistrationStatus(String statusName) {
        this.statusName = statusName;
    }

    public String getStatusName() {
        return statusName;
    }
}
