package com.guzov.pibot.action;

import com.guzov.pibot.security.RegistrationService;
import com.guzov.pibot.security.RegistrationStatus;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.SendMessage;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public abstract class AbstractAction implements Action {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractAction.class);

    public abstract List<RegistrationStatus> getAllowedRegistrationStatuses();

    public abstract BaseRequest doGetRequest(Message message, Action previousAction);

    @Override
    public BaseRequest getRequest(Message message, Action previousAction) {
        if (message == null) {
            throw new IllegalArgumentException("Message must not be null");
        }
        RegistrationStatus registrationStatus = RegistrationService
                .getInstance().getRegistrationStatus(message.from().username());
        if (getAllowedRegistrationStatuses().contains(registrationStatus)) {
            return doGetRequest(message, previousAction);
        } else {
            LOGGER.info(
                    "Attempting to execute action \"{}\" with \"{}\" registration status, sending warning message",
                    getName(),
                    registrationStatus.getStatusName());
            return new SendMessage(message.chat().id(), getForbiddenText(registrationStatus));
        }
    }

    @NotNull
    private String getForbiddenText(RegistrationStatus registrationStatus) {
        return String.join("",
                "Your status \"",
                registrationStatus.getStatusName(),
                "\" is not eligible for \"",
                getName(),
                "\" action");
    }


}
