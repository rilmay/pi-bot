package com.guzov.pibot.action;

import com.guzov.pibot.Constants;
import com.guzov.pibot.security.RegistrationResult;
import com.guzov.pibot.security.RegistrationService;
import com.guzov.pibot.security.RegistrationStatus;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.SendMessage;

import java.util.List;

public class FreeTextAction extends AbstractAction {

    public static final String FREE_TEXT = "free text";

    public BaseRequest doGetRequest(Message message, Action previousAction) {
        Long chatId = message.chat().id();
        if (previousAction != null && previousAction.getName().equals(RegisterAction.REGISTER)) {
            String password = message.text();
            String userName = message.from().username();
            RegistrationResult result = RegistrationService.getInstance().register(userName, password);
            return new SendMessage(chatId, result.getResponseMessage());
        } else if (!Constants.START_COMMAND.equals(message.text())) {
            return new SendMessage(chatId, "Sorry, I don't know this command");
        } else {
            return null;
        }
    }

    public List<RegistrationStatus> getAllowedRegistrationStatuses() {
        return List.of(RegistrationStatus.REGISTERED, RegistrationStatus.NOT_REGISTERED);
    }

    @Override
    public String getName() {
        return FREE_TEXT;
    }
}
