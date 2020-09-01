package com.dnieln7.java.generic.request.utils;

/**
 * Collection of Http Methods.
 *
 * @author dnieln7
 */
public enum RequestMethod {
    GET("GET"),
    POST("POST"),
    PUT("PUT"),
    DELETE("DELETE"),
    PATCH("PATCH"),
    OPTIONS("OPTIONS");

    private final String method;

    RequestMethod(String method) {
        this.method = method;
    }
}
