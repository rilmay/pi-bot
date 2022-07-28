package com.guzov.pibot.action;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.BaseRequest;

public interface Action {
    BaseRequest getRequest(Message message, Action previousAction);

    String getName();

    default void doPostProcessing(BaseRequest request) {
    }
}
