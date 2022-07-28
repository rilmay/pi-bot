package com.guzov.pibot.common;

import com.guzov.pibot.Constants;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class CliParser {
    private CliParser() {
    }

    public static Map<String, String> parseCli(String[] args) {
        Options options = getConfiguredOptions();

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = null;
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Program CLI args is not filled correctly");
        }
        CommandLine finalCmd = cmd;
        return Arrays.stream(cmd.getOptions()).collect(Collectors.toMap(Option::getLongOpt, finalCmd::getOptionValue));
    }

    private static Options getConfiguredOptions() {
        Options options = new Options();

        Option resolver = new Option(
                "r", Constants.RESOLVER_CLI_OPTION_NAME, true, "External variable resolver");
        resolver.setRequired(false);
        options.addOption(resolver);

        Option data = new Option(
                "d", Constants.DATA_CLI_OPTION_NAME, true, "Data for finding variables");
        data.setRequired(false);
        options.addOption(data);
        return options;
    }
}
