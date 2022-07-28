package com.guzov.pibot.security;

import com.guzov.pibot.Constants;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public class RegistrationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(RegistrationService.class);
    private static final char USERNAMES_DELIMITER = '@';
    private static RegistrationService instance;
    private static final ReentrantLock lock = new ReentrantLock();
    private final Map<String, UserInfo> userInfoByUserName = new HashMap<>();
    private String password;
    private String authenticationStrategy;
    private int maxRegisteredUsers;
    private int maxRegistrationAttempts;
    private List<String> userNames;

    private RegistrationService(Properties properties) {
        LOGGER.info("Initializing registration service");
        readConfig(properties);
        checkConfig();
        LOGGER.info("Registration service is initialized with \"{}\" authentication strategy", authenticationStrategy);
        if (Constants.AUTHENTICATION_STRATEGY_USERNAMES.equals(authenticationStrategy)) {
            LOGGER.info("Registered usernames: {}", userNames);
        } else if (Constants.AUTHENTICATION_STRATEGY_PASSWORD.equals(authenticationStrategy)) {
            LOGGER.info(
                    "Maximum registered users: {}, maximum registration attempts: {}",
                    maxRegisteredUsers,
                    maxRegistrationAttempts
            );
        }
    }

    private void checkConfig() {
        if (!userNamesStrategyCorrect() && !passwordStrategyCorrect()) {
            throw new IllegalStateException("Authentication is not properly configured");
        }
    }

    private boolean userNamesStrategyCorrect() {
        return Constants.AUTHENTICATION_STRATEGY_USERNAMES.equals(authenticationStrategy) && !userNames.isEmpty();
    }

    private boolean passwordStrategyCorrect() {
        return Constants.AUTHENTICATION_STRATEGY_PASSWORD.equals(authenticationStrategy) &&
                maxRegisteredUsers > 0 &&
                maxRegistrationAttempts > 0 &&
                StringUtils.isNotBlank(password);
    }

    private void readConfig(Properties properties) {
        authenticationStrategy = properties.getProperty(Constants.BOT_USER_AUTHENTICATION_STRATEGY_KEY);
        if (authenticationStrategy.equals(Constants.AUTHENTICATION_STRATEGY_USERNAMES)) {
            String concatenatedUserNames = properties.getProperty(Constants.ALLOWED_USERNAMES_KEY);
            if (StringUtils.isBlank(concatenatedUserNames)) {
                throw new IllegalStateException("User names should be specified in selected authentication strategy");
            }
            userNames = getUserNames(concatenatedUserNames);
        } else if (authenticationStrategy.equals(Constants.AUTHENTICATION_STRATEGY_PASSWORD)) {
            password = properties.getProperty(Constants.BOT_USER_PASSWORD_KEY);
            maxRegisteredUsers = Integer.parseInt(properties.getProperty(Constants.MAX_REGISTERED_USERS_KEY));
            maxRegistrationAttempts = Integer.parseInt(properties.getProperty(Constants.USER_MAX_REGISTRATION_ATTEMPTS_KEY));
        }
    }

    private List<String> getUserNames(String concatenatedUserNames) {
        String[] usernames = StringUtils.split(concatenatedUserNames, USERNAMES_DELIMITER);
        return Arrays.stream(usernames).collect(Collectors.toList());
    }

    public static void init(Properties properties) {
        lock.lock();
        try {
            if (instance == null) {
                if (properties == null) {
                    throw new IllegalArgumentException("Properties for registration should be specified");
                }
                instance = new RegistrationService(properties);
            } else {
                throw new IllegalStateException("Handler is already initialized");
            }
        } finally {
            lock.unlock();
        }
    }

    public static RegistrationService getInstance() {
        if (instance == null) {
            throw new IllegalStateException("Registration service is not initialized");
        } else {
            return instance;
        }
    }

    public RegistrationResult register(String userName, String password) {
        if (authenticationStrategy.equals(Constants.AUTHENTICATION_STRATEGY_USERNAMES)) {
            return RegistrationResult.NOT_SUPPORTED;
        } else if (authenticationStrategy.equals(Constants.AUTHENTICATION_STRATEGY_PASSWORD)) {
            UserInfo userInfo;
            if (userInfoByUserName.containsKey(userName)) {
                userInfo = userInfoByUserName.get(userName);
                userInfo.setAttemptsToRegister(userInfo.getAttemptsToRegister() + 1);
            } else {
                userInfo = new UserInfo(RegistrationStatus.NOT_REGISTERED, 1);
            }

            if (userInfo.getRegistrationStatus() == RegistrationStatus.REGISTERED) {
                return RegistrationResult.ALREADY_REGISTERED;
            } else if (isMaxRegisteredCountExceeded()) {
                LOGGER.info(
                        "User with username \"{}\" is trying to register when max registered users number is exceeded",
                        userName
                );
                return RegistrationResult.FAILURE;
            } else if (isMaxAttemptsExceeded(userInfo.getAttemptsToRegister())) {
                LOGGER.info("Max logging attempts for user \"{}\" is exceeded", userName);
                return RegistrationResult.FAILURE;
            } else if (isPasswordCorrect(password)) {
                userInfo.setRegistrationStatus(RegistrationStatus.REGISTERED);
                userInfoByUserName.put(userName, userInfo);
                LOGGER.info("User with username \"{}\" is successfully registered", userName);
                return RegistrationResult.SUCCESS;
            } else {
                LOGGER.info("User with username \"{}\" has failed to register", userName);
                userInfoByUserName.put(userName, userInfo);
                return RegistrationResult.FAILURE;
            }
        } else {
            throw new IllegalStateException("Selected authentication strategy is not supported");
        }
    }

    private boolean isMaxAttemptsExceeded(int attemptNumber) {
        return attemptNumber >= maxRegistrationAttempts;
    }

    private boolean isPasswordCorrect(String inputPassword) {
        return password.equals(inputPassword);
    }

    private boolean isMaxRegisteredCountExceeded() {
        return userInfoByUserName
                .values()
                .stream()
                .filter(userInfo -> userInfo.getRegistrationStatus() == RegistrationStatus.REGISTERED)
                .count() > maxRegisteredUsers;
    }

    public RegistrationStatus getRegistrationStatus(String userName) {
        if (authenticationStrategy.equals(Constants.AUTHENTICATION_STRATEGY_USERNAMES)) {
            if (userNames.contains(userName)) {
                return RegistrationStatus.REGISTERED;
            } else {
                return RegistrationStatus.NOT_REGISTERED;
            }
        } else if (authenticationStrategy.equals(Constants.AUTHENTICATION_STRATEGY_PASSWORD)) {
            UserInfo userInfo = userInfoByUserName.get(userName);
            if (userInfo != null && userInfo.getRegistrationStatus() != null) {
                return userInfo.getRegistrationStatus();
            } else {
                return RegistrationStatus.NOT_REGISTERED;
            }
        } else {
            throw new IllegalStateException("Selected authentication strategy is not supported");
        }
    }
}
