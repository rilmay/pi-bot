package com.guzov.pibot.security;

public class UserInfo {
    private RegistrationStatus registrationStatus;
    private int attemptsToRegister;

    public UserInfo(RegistrationStatus registrationStatus, int attemptsToRegister) {
        this.registrationStatus = registrationStatus;
        this.attemptsToRegister = attemptsToRegister;
    }

    public int getAttemptsToRegister() {
        return attemptsToRegister;
    }

    public void setAttemptsToRegister(int attemptsToRegister) {
        this.attemptsToRegister = attemptsToRegister;
    }

    public RegistrationStatus getRegistrationStatus() {
        return registrationStatus;
    }

    public void setRegistrationStatus(RegistrationStatus registrationStatus) {
        this.registrationStatus = registrationStatus;
    }
}
