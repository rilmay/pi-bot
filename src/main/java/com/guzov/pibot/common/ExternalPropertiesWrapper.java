package com.guzov.pibot.common;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;
import java.util.Properties;
import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExternalPropertiesWrapper extends Properties {
    private static final char[] SPECIAL_CHARACTERS = new char[]{'{', '}', '$'};
    private static final Pattern EXTERNAL_VARIABLE_PATTERN = Pattern.compile("(?<=^\\$\\{)[a-zA-Z_]+(?=}$)");
    private static final String DEFAULT_POSTFIX = ".default";
    private final transient UnaryOperator<String> externalPropertiesResolver;

    public ExternalPropertiesWrapper(UnaryOperator<String> externalPropertiesResolver) {
        this.externalPropertiesResolver = externalPropertiesResolver;
    }

    @Override
    public String getProperty(String key) {
        return getProperty(key, true);
    }

    private String getProperty(String key, boolean searchForDefault) {
        String property = super.getProperty(key);
        if (StringUtils.containsAny(property, SPECIAL_CHARACTERS)) {
            Matcher matcher = EXTERNAL_VARIABLE_PATTERN.matcher(property);
            if (matcher.find()) {
                String externalVariableKey = matcher.group();
                property = externalPropertiesResolver.apply(externalVariableKey);
                if (StringUtils.isBlank(property) && searchForDefault) {
                    property = getProperty(getDefaultKey(key), false);
                }
            }
        }
        return property;
    }

    private String getDefaultKey(String key) {
        return String.join("", key, DEFAULT_POSTFIX);
    }

    @Override
    public synchronized boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ExternalPropertiesWrapper that = (ExternalPropertiesWrapper) o;
        return Objects.equals(externalPropertiesResolver, that.externalPropertiesResolver);
    }

    @Override
    public synchronized int hashCode() {
        return Objects.hash(super.hashCode(), externalPropertiesResolver);
    }
}

