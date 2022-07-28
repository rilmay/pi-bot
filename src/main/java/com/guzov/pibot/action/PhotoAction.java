package com.guzov.pibot.action;


import com.guzov.pibot.Constants;
import com.guzov.pibot.common.CameraService;
import com.guzov.pibot.security.RegistrationStatus;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.SendPhoto;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class PhotoAction extends AbstractAction {
    public static final String PHOTO = "photo";

    public BaseRequest doGetRequest(Message message, Action previousAction) {
        return getImageRequest(message.chat().id());
    }

    public List<RegistrationStatus> getAllowedRegistrationStatuses() {
        return Collections.singletonList(RegistrationStatus.REGISTERED);
    }

    @Override
    public String getName() {
        return PHOTO;
    }


    private SendPhoto getImageRequest(Long chatId) {
        byte[] image = getImage();
        return new SendPhoto(chatId, image);
    }

    private byte[] getImage() {
        BufferedImage image = CameraService.getInstance().getImage();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, Constants.JPG_PHOTO_FORMAT, baos);
        } catch (IOException e) {
            throw new IllegalStateException("Error while getting camera image");
        }
        return baos.toByteArray();
    }

}
