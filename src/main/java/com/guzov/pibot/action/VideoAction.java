package com.guzov.pibot.action;

import com.guzov.pibot.common.CameraService;
import com.guzov.pibot.security.RegistrationStatus;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.SendAnimation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

public class VideoAction extends AbstractAction {
    private static final Logger LOGGER = LoggerFactory.getLogger(VideoAction.class);
    public static final String VIDEO = "video";

    public BaseRequest doGetRequest(Message message, Action previousAction) {
        return getVideo(message.chat().id());
    }

    @Override
    public List<RegistrationStatus> getAllowedRegistrationStatuses() {
        return Collections.singletonList(RegistrationStatus.REGISTERED);
    }

    @Override
    public String getName() {
        return VIDEO;
    }

    private SendAnimation getVideo(Long chatId) {
        File video = CameraService.getInstance().getVideoGif();
        return new SendAnimation(chatId, video);
    }

    @Override
    public void doPostProcessing(BaseRequest request) {
        String fileName = request.getFileName();
        if (StringUtils.isNotBlank(fileName)) {
            try {
                Files.delete(Path.of(fileName));
            } catch (IOException e) {
                LOGGER.warn("File {} was not deleted", fileName);
            }
        }
    }
}
