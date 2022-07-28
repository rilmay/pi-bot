package com.guzov.pibot;

import com.guzov.pibot.common.AdvancedPropertiesProvider;
import com.guzov.pibot.common.CliParser;
import com.guzov.pibot.common.ExternalVariablesResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Properties;

public class App {
    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        LOGGER.info("Starting application");
        Map<String, String> cliArgsMap = CliParser.parseCli(args);
        String resolverName = cliArgsMap.get(Constants.RESOLVER_CLI_OPTION_NAME);
        String variablesData = cliArgsMap.get(Constants.DATA_CLI_OPTION_NAME);
        Properties botProperties = AdvancedPropertiesProvider
                .getInstance()
                .readProperties(
                        Constants.BOT_PROPERTIES_FILE_NAME,
                        ExternalVariablesResolver.findByName(resolverName).get(variablesData)
                );
        Bot bot = new Bot(botProperties);
        bot.run();
    }
}
