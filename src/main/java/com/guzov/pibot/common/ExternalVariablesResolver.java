package com.guzov.pibot.common;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Map;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public enum ExternalVariablesResolver {
    ENVIRONMENT_VARIABLE_RESOLVER {
        @Override
        public UnaryOperator<String> get(Object data) {
            return System::getenv;
        }

        @Override
        public String getResolverName() {
            return "environment";
        }
    },
    CLI_ARGS_RESOLVER {
        @Override
        public UnaryOperator<String> get(Object data) {
            if (!(data instanceof String) || StringUtils.isBlank(String.valueOf(data))) {
                throw new IllegalStateException("Data for cli args resolver should be in string format and not blank");
            }
            String input = String.valueOf(data);
            Map<String, String> externalVariables = Arrays
                    .stream(StringUtils.split(input, ','))
                    .collect(Collectors.toMap(
                            s -> StringUtils.split(s, "=", 2)[0],
                            s -> StringUtils.split(s, "=", 2)[1])
                    );
            return externalVariables::get;
        }

        @Override
        public String getResolverName() {
            return "cli";
        }
    };

    public abstract UnaryOperator<String> get(Object data);

    public abstract String getResolverName();

    public static ExternalVariablesResolver findByName(String name) {
        return Arrays.stream(ExternalVariablesResolver.values())
                .filter(resolver -> resolver.getResolverName().equals(name))
                .findFirst().orElseGet(() -> ENVIRONMENT_VARIABLE_RESOLVER);
    }
}
