package com.guzov.pibot.action;

import com.guzov.pibot.Constants;
import com.pengrad.telegrambot.model.Message;

import java.util.HashMap;
import java.util.Map;

public class ActionFactory {
    private Map<String, Action> actionMap;
    private Action defaultAction = new FreeTextAction();

    public ActionFactory() {
        initActions();
    }

    private void initActions() {
        actionMap = new HashMap<>();
        actionMap.put(Constants.PHOTO_COMMAND, new PhotoAction());
        actionMap.put(Constants.VIDEO_COMMAND, new VideoAction());
        actionMap.put(Constants.REGISTER_COMMAND, new RegisterAction());
    }

    public Action getByMessage(Message message) {
        Action action = actionMap.get(message.text());
        if (action == null) {
            return defaultAction;
        } else {
            return action;
        }
    }
}
