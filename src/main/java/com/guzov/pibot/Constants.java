package com.guzov.pibot;

public final class Constants {
    private Constants() {
    }

    public static final String RESOLVER_CLI_OPTION_NAME = "resolver";
    public static final String DATA_CLI_OPTION_NAME = "data";

    public static final String BOT_USER_AUTHORIZATION_STRATEGY_KEY = "bot.user.authorization.strategy";
    public static final String BOT_USER_PASSWORD_KEY = "bot.user.password";
    public static final String MAX_REGISTERED_USERS_KEY = "bot.user.max_registered";
    public static final String USER_MAX_REGISTRATION_ATTEMPTS_KEY = "bot.user.max_registration_attempts";
    public static final String ALLOWED_USERNAMES_KEY = "bot.allowed_usernames";
    public static final String WEBCAM_NUMBER_KEY = "bot.webcam.number";
    public static final String TOKEN_KEY = "bot.telegram.token";
    public static final String VIDEO_FRAMES_COUNT_KEY = "bot.webcam.video.frames_count";
    public static final String VIDEO_SPEED_KEY = "bot.webcam.video.speed";

    public static final String AUTHORIZATION_STRATEGY_USERNAMES = "usernames";
    public static final String AUTHORIZATION_STRATEGY_PASSWORD = "password";

    public static final String BOT_PROPERTIES_FILE_NAME = "bot.properties";
    public static final String JPG_PHOTO_FORMAT = "jpg";

    public static final String PHOTO_COMMAND = "/photo";
    public static final String VIDEO_COMMAND = "/video";
    public static final String REGISTER_COMMAND = "/register";
    public static final String START_COMMAND = "/start";


}
