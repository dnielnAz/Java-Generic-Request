package com.dnieln7.java.generic.request.exception;

import com.google.gson.JsonObject;


/**
 * Exception thrown if theres an error when sending an http request.
 *
 * @author dnieln7
 */
public class ResponseException extends Exception {

    private final transient JsonObject serverError;

    public ResponseException(String message, JsonObject serverError) {
        super(message);
        this.serverError = serverError;
    }

    /**
     * @return Error body returned by the server on a {@link JsonObject} format.
     */
    public JsonObject getServerError() {
        return serverError;
    }
}
