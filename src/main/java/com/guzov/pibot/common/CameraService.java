package com.guzov.pibot.common;

import com.guzov.pibot.Constants;
import com.squareup.gifencoder.FloydSteinbergDitherer;
import com.squareup.gifencoder.GifEncoder;
import com.squareup.gifencoder.ImageOptions;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameGrabber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CameraService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CameraService.class);
    private static final Lock lock = new ReentrantLock();
    private final int framesInGif;
    private final int deviceNumber;
    private static CameraService instance;
    public static final String VIDEO_EXTENSION = ".gif";
    private static final ReentrantLock cameraLock = new ReentrantLock();
    private final double speed;


    private CameraService(Properties properties) {
        this.deviceNumber = Integer.parseInt(properties.getProperty(Constants.WEBCAM_NUMBER_KEY));
        this.framesInGif = Integer.parseInt(properties.getProperty(Constants.VIDEO_FRAMES_COUNT_KEY));
        this.speed = Double.parseDouble(properties.getProperty(Constants.VIDEO_SPEED_KEY));
        checkProperties();
    }

    private void checkProperties() {
        if (framesInGif < 2) {
            throw new IllegalArgumentException("Frames in video was defined incorrectly");
        }
        if (speed <= 0) {
            throw new IllegalArgumentException("Video speed was defined incorrectly");
        }
    }

    public static void init(Properties properties) {
        lock.lock();
        try {
            if (instance == null) {
                instance = new CameraService(properties);
            } else {
                throw new IllegalStateException("Camera is already initialized");
            }
        } finally {
            lock.unlock();
        }
    }

    public static CameraService getInstance() {
        return instance;
    }

    public BufferedImage getImage() {
        cameraLock.lock();
        LOGGER.info("Taking a photo");
        try (FrameGrabber grabber = new OpenCVFrameGrabber(deviceNumber);
             Java2DFrameConverter paintConverter = new Java2DFrameConverter()) {
            grabber.start();
            return getBufferedImage(grabber, paintConverter);
        } catch (IOException e) {
            throw new IllegalStateException("Error while getting camera image");
        } finally {
            cameraLock.unlock();
        }
    }

    private static BufferedImage getBufferedImage(
            FrameGrabber grabber,
            Java2DFrameConverter paintConverter) throws FrameGrabber.Exception {
        Frame frame = grabber.grab();
        return paintConverter.getBufferedImage(frame, 1);
    }

    private String getFileName() {
        return String.join("", System.getProperty("user.dir"), System.getProperty("file.separator"), String.valueOf(UUID.randomUUID()), VIDEO_EXTENSION);
    }

    public File getVideoGif() {
        cameraLock.lock();
        LOGGER.info("Taking a video");
        String fileName = getFileName();
        try (FrameGrabber grabber = new OpenCVFrameGrabber(deviceNumber);
             Java2DFrameConverter paintConverter = new Java2DFrameConverter();
             FileOutputStream outputStream = new FileOutputStream(fileName)) {
            Long lastFrameTime = null;
            GifEncoder gifEncoder = null;
            grabber.start();
            for (int i = 1; i <= framesInGif; i++) {
                LOGGER.info("Taking a picture {} out of {}", i, framesInGif);
                BufferedImage image = getBufferedImage(grabber, paintConverter);
                long currentTime = System.currentTimeMillis();
                lastFrameTime = lastFrameTime == null ?
                        currentTime :
                        lastFrameTime;
                gifEncoder = gifEncoder == null ?
                        new GifEncoder(outputStream, image.getWidth(), image.getHeight(), 0) :
                        gifEncoder;
                long delay = currentTime - lastFrameTime;
                long videoDelay = getGifDelay(delay);
                lastFrameTime = currentTime;
                LOGGER.info("Current photo delay {}, video delay {}", delay, videoDelay);
                gifEncoder.addImage(convertImageToArray(image), getOptions(videoDelay));
            }
            gifEncoder.finishEncoding();
            LOGGER.info("Finished GIF encoding");
            return new File(fileName);
        } catch (IOException e) {
            throw new IllegalStateException("Error while getting camera image");
        } finally {
            cameraLock.unlock();
        }
    }

    private ImageOptions getOptions(long delay) {
        ImageOptions options = new ImageOptions();
        options.setDelay(delay, TimeUnit.MILLISECONDS);
        options.setDitherer(FloydSteinbergDitherer.INSTANCE);
        return options;

    }

    private long getGifDelay(long realDelay) {
        return (long) (realDelay / speed);
    }

    public int[][] convertImageToArray(BufferedImage bufferedImage) {
        int[][] rgbArray = new int[bufferedImage.getHeight()][bufferedImage.getWidth()];
        for (int i = 0; i < bufferedImage.getHeight(); i++) {
            for (int j = 0; j < bufferedImage.getWidth(); j++) {
                rgbArray[i][j] = bufferedImage.getRGB(j, i);
            }
        }
        return rgbArray;
    }
}
