package com.dnieln7.java.generic.request;

import com.dnieln7.java.generic.request.exception.BuilderException;
import com.dnieln7.java.generic.request.exception.ResponseException;
import com.dnieln7.java.generic.request.utils.RequestMethod;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Generic class to perform HTTP requests
 *
 * @author dnieln7
 */
public class GenericRequestSession {

    /**
     * Builder to create new instances of {@link GenericRequestSession}
     *
     * @author dnieln7
     */
    public static class Builder {
        private String url;
        private RequestMethod requestMethod;
        private Integer responseCode;
        private Boolean doOutput;
        private Map<String, String> requestProperties;

        /**
         * Creates a new instance of {@link Builder} with the default configuration.
         * <ul>
         *     <li>{@link RequestMethod} -> GET</li>
         *     <li>Response Code -> 200</li>
         *     <li>Do Output -> false</li>
         *     <li>Request properties -> <br> {"Content-Type" : "application/json", "Accept" : "application/json"}</li>
         * </ul>
         */
        public Builder(String url) {
            this.url = url;
            this.requestMethod = RequestMethod.GET;
            this.responseCode = 200;
            this.doOutput = false;
            this.requestProperties = new HashMap<>();
            this.requestProperties.put("Content-Type", "application/json");
            this.requestProperties.put("Accept", "application/json");
        }

        /**
         * Configure destination url.
         *
         * @param url Valid url to send requests.
         * @return The current {@link Builder} instance.
         */
        public Builder to(String url) {
            this.url = url;

            return this;
        }

        /**
         * Sets the Http method of the request.
         *
         * @param requestMethod A {@link RequestMethod} item.
         * @return The current {@link Builder} instance.
         */
        public Builder withMethod(RequestMethod requestMethod) {
            this.requestMethod = requestMethod;

            return this;
        }

        /**
         * Sets the expected response code, if the response code returned by the server
         * is different is different from the one provided,
         * the {@link GenericRequestSession} object will return an exception of type {@link ResponseException}.
         *
         * @param responseCode Expected response code.
         * @return The current {@link Builder} instance.
         */
        public Builder withResponseCode(Integer responseCode) {
            this.responseCode = responseCode != null ? responseCode : 200;

            return this;
        }

        /**
         * Set to true if the request has a body, defaults to false.
         *
         * @param output Whether the request should have a body.
         * @return The current {@link Builder} instance.
         */
        public Builder withOutput(Boolean output) {
            this.doOutput = output != null ? output : Boolean.FALSE;

            return this;
        }

        /**
         * Sets the request properties.
         *
         * @param requestProperties A {@link Map} containing the key - value, properties.
         * @return The current {@link Builder} instance.
         */
        public Builder withRequestProperties(Map<String, String> requestProperties) {
            this.requestProperties = requestProperties;

            return this;
        }

        /**
         * Creates a new instance of {@link GenericRequestSession} using the current configuration.
         *
         * @return A new instance of {@link GenericRequestSession}.
         * @throws BuilderException If there´s an error in the process.
         */
        public GenericRequestSession build() throws BuilderException {
            URL urlObject;
            GenericRequestSession genericRequestSession = null;

            try {
                urlObject = new URL(this.url);
            } catch (MalformedURLException e) {
                throw new BuilderException("There is a problem with the provided url: " + this.url);
            }

            try {
                genericRequestSession = new GenericRequestSession(
                        (HttpURLConnection) urlObject.openConnection(),
                        this.responseCode,
                        this.doOutput,
                        this.requestProperties
                );

                genericRequestSession.setRequestMethod(this.requestMethod);

            } catch (IOException e) {
                Logger.getLogger(Builder.class.getName()).log(Level.SEVERE, "There was an error", e);
            }

            return genericRequestSession;
        }
    }

    private final HttpURLConnection connection;
    private final Integer responseCode;

    private GenericRequestSession(HttpURLConnection connection, Integer responseCode, Boolean doOutput, Map<String, String> properties) {
        this.connection = connection;
        this.connection.setDoOutput(doOutput);
        properties.forEach((key, value) -> this.connection.setRequestProperty(key, value));
        this.responseCode = responseCode;
    }

    private void setRequestMethod(RequestMethod requestMethod) {
        try {
            connection.setRequestMethod(requestMethod.toString());
        } catch (ProtocolException e) {
            logError(e);
        }
    }

    private void logError(Throwable error) {
        Logger.getLogger(GenericRequestSession.class.getName()).log(Level.SEVERE, "There was an error", error);
    }

    /**
     * Sends a basic request using the configuration of the actual {@link GenericRequestSession} instance.
     *
     * @param typeClass Type to cast the response data.
     * @return The response data casted to the configured type.
     * @throws ResponseException If theres an error with the request.
     */
    public <T> T sendRequest(Class<T> typeClass) throws ResponseException {
        BufferedReader response;

        try {
            if (connection.getResponseCode() != responseCode) {

                response = new BufferedReader(new InputStreamReader(connection.getErrorStream()));

                throw new ResponseException(
                        "Failed: Http error code: " + connection.getResponseCode(),
                        new JsonParser().parse(response.readLine()).getAsJsonObject()
                );
            }

            response = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            return new Gson().fromJson(response.readLine(), typeClass);
        } catch (IOException e) {
            logError(e);
            return null;
        } finally {
            connection.disconnect();
        }
    }

    /**
     * Sends a basic request using the configuration of the actual {@link GenericRequestSession} instance.
     *
     * @param typeClass Array type to cast the response data.
     * @return The response data casted to the configured type.
     * @throws ResponseException If theres an error with the request.
     */
    public <T> List<T> sendRequestExpectingList(Class<T[]> typeClass) throws ResponseException {
        BufferedReader response;

        try {
            if (connection.getResponseCode() != responseCode) {

                response = new BufferedReader(new InputStreamReader(connection.getErrorStream()));

                throw new ResponseException(
                        "Failed: Http error code: " + connection.getResponseCode(),
                        new JsonParser().parse(response.readLine()).getAsJsonObject()
                );
            }

            response = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            return Arrays.asList(new Gson().fromJson(response.readLine(), typeClass));
        } catch (IOException e) {
            logError(e);
            return new ArrayList<>();
        } finally {
            connection.disconnect();
        }
    }

    /**
     * Sends a request with a body using the configuration of the actual {@link GenericRequestSession} instance.
     *
     * @param typeClass Type to cast the response data.
     * @param body      An object to send as the body.
     * @return The response data casted to the supplied type.
     * @throws ResponseException If theres an error with the request.
     */
    public <T> T sendRequestWithBody(Class<T> typeClass, Object body) throws ResponseException {
        BufferedReader response;

        try {
            String jsonInput = new Gson().toJson(body);
            OutputStream output = connection.getOutputStream();

            output.write(jsonInput.getBytes());
            output.flush();

            if (connection.getResponseCode() != responseCode) {

                response = new BufferedReader(new InputStreamReader(connection.getErrorStream()));

                throw new ResponseException(
                        "Failed: Http error code: " + connection.getResponseCode(),
                        new JsonParser().parse(response.readLine()).getAsJsonObject()
                );
            }

            response = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            return new Gson().fromJson(response.readLine(), typeClass);
        } catch (IOException e) {
            logError(e);
            return null;
        } finally {
            connection.disconnect();
        }
    }

    /**
     * Sends a request with a body using the configuration of the actual {@link GenericRequestSession} instance.
     *
     * @param typeClass Type to cast the response data.
     * @param body      A {@link JsonObject} instance to send as the body.
     * @return The response data casted to the supplied type.
     * @throws ResponseException If theres an error with the request.
     */
    public <T> T sendRequestWithBody(Class<T> typeClass, JsonObject body) throws ResponseException {
        BufferedReader response;

        try {
            String jsonInput = body.toString();
            OutputStream output = connection.getOutputStream();

            output.write(jsonInput.getBytes());
            output.flush();

            if (connection.getResponseCode() != responseCode) {

                response = new BufferedReader(new InputStreamReader(connection.getErrorStream()));

                throw new ResponseException(
                        "Failed: Http error code: " + connection.getResponseCode(),
                        new JsonParser().parse(response.readLine()).getAsJsonObject()
                );
            }

            response = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            return new Gson().fromJson(response.readLine(), typeClass);
        } catch (IOException e) {
            logError(e);
            return null;
        } finally {
            connection.disconnect();
        }
    }

    /**
     * Sends a request with a body using the configuration of the actual {@link GenericRequestSession} instance.
     *
     * @param typeClass Type to cast the response data.
     * @param body      A string in json format to send as the body.
     * @return The response data casted to the supplied type.
     * @throws ResponseException If theres an error with the request.
     */
    public <T> T sendRequestWithBody(Class<T> typeClass, String body) throws ResponseException {
        BufferedReader response;

        try {
            OutputStream output = connection.getOutputStream();

            output.write(body.getBytes());
            output.flush();

            if (connection.getResponseCode() != responseCode) {

                response = new BufferedReader(new InputStreamReader(connection.getErrorStream()));

                throw new ResponseException(
                        "Failed: Http error code: " + connection.getResponseCode(),
                        new JsonParser().parse(response.readLine()).getAsJsonObject()
                );
            }

            response = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            return new Gson().fromJson(response.readLine(), typeClass);
        } catch (IOException e) {
            logError(e);
            return null;
        } finally {
            connection.disconnect();
        }
    }
}
