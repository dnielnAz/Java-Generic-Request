package com.dnieln7.java.generic.request.utils;

import java.util.Map;

/**
 * Collection of common Request properties.
 *
 * @author dnieln7
 */
public class RequestProperties {
    public static final Map<String, String> JSON_PROPERTIES = Map.of(
            "Content-Type", "application/json",
            "Accept", "application/json"
    );

    private RequestProperties() {
    }
}
