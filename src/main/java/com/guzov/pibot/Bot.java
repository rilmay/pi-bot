package com.guzov.pibot;

import com.guzov.pibot.action.Action;
import com.guzov.pibot.action.ActionFactory;
import com.guzov.pibot.common.CameraService;
import com.guzov.pibot.security.RegistrationService;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.BaseRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Bot {
    private static final Logger LOGGER = LoggerFactory.getLogger(Bot.class);
    private final TelegramBot telegramBot;
    private final Map<Long, Action> actionsByChatIds = new HashMap<>();
    private final String telegramToken;
    private final ActionFactory actionFactory;

    public Bot(Properties properties) {
        LOGGER.info("Initializing telegram bot");
        if (properties == null) {
            throw new IllegalArgumentException("Properties for telegram bot should be specified");
        }
        telegramToken = properties.getProperty(Constants.TOKEN_KEY);
        if (StringUtils.isBlank(telegramToken)) {
            throw new IllegalStateException("Telegram token should be specified");
        }
        telegramBot = new TelegramBot(telegramToken);
        RegistrationService.init(properties);
        CameraService.init(properties);
        actionFactory = new ActionFactory();

    }

    public void run() {
        LOGGER.info("Setting update listeners");
        telegramBot.setUpdatesListener(updates -> {
            updates.forEach(this::process);
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
        LOGGER.info("Bot is up and running");
    }

    private void process(Update update) {
        Message message = update.message();
        Action action = null;
        BaseRequest request = null;
        if (message != null) {
            Long chatId = message.chat().id();
            Action previousAction = actionsByChatIds.get(chatId);
            action = actionFactory.getByMessage(message);
            request = action.getRequest(message, previousAction);
            actionsByChatIds.put(chatId, action);
        }
        if (request != null) {
            telegramBot.execute(request);
            action.doPostProcessing(request);
        }
    }
}
