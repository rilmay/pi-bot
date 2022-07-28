package com.guzov.pibot.action;

import com.guzov.pibot.security.RegistrationStatus;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.SendMessage;

import java.util.Collections;
import java.util.List;

public class RegisterAction extends AbstractAction {

    public static final String REGISTER = "register";

    public BaseRequest doGetRequest(Message message, Action previousAction) {
        return new SendMessage(message.chat().id(), "Please type password");
    }

    public List<RegistrationStatus> getAllowedRegistrationStatuses() {
        return Collections.singletonList(RegistrationStatus.NOT_REGISTERED);
    }

    @Override
    public String getName() {
        return REGISTER;
    }
}
