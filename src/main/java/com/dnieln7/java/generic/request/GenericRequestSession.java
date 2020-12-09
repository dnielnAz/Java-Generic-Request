package com.dnieln7.java.generic.request;

import com.dnieln7.java.generic.request.exception.BuilderException;
import com.dnieln7.java.generic.request.exception.ResponseException;
import com.dnieln7.java.generic.request.utils.RequestMethod;
import com.google.gson.Gson;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Generic class to perform HTTP requests
 *
 * @author dnieln7
 */
public class GenericRequestSession<T> {

    /**
     * Builder to create new instances of {@link GenericRequestSession}
     *
     * @author dnieln7
     */
    public static class Builder<T> {
        private String url;
        private Class<T> typeClass;
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
         * </ul>
         */
        public Builder() {
            this.requestMethod = RequestMethod.GET;
            this.responseCode = 200;
            this.doOutput = false;
        }

        /**
         * Configure destination url.
         *
         * @param url Valid url to send requests.
         * @return The current {@link Builder} instance.
         */
        public Builder<T> to(String url) {
            this.url = url;

            return this;
        }

        /**
         * Sets the type to cast the response data.
         *
         * @return The current {@link Builder} instance.
         */
        public Builder<T> ofType(Class<T> typeClass) {
            this.typeClass = typeClass;
            return this;
        }

        /**
         * Sets the Http method of the request.
         *
         * @param requestMethod A {@link RequestMethod} item.
         * @return The current {@link Builder} instance.
         */
        public Builder<T> withMethod(RequestMethod requestMethod) {
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
        public Builder<T> withResponseCode(Integer responseCode) {
            this.responseCode = responseCode != null ? responseCode : 200;

            return this;
        }

        /**
         * Set to true if the request has a body, defaults to false.
         *
         * @param output Whether the request should have a body.
         * @return The current {@link Builder} instance.
         */
        public Builder<T> withOutput(Boolean output) {
            this.doOutput = output != null ? output : Boolean.FALSE;

            return this;
        }

        /**
         * Sets the request properties.
         *
         * @param requestProperties A {@link Map} containing the key - value, properties.
         * @return The current {@link Builder} instance.
         */
        public Builder<T> withRequestProperties(Map<String, String> requestProperties) {
            this.requestProperties = requestProperties;

            return this;
        }

        /**
         * Creates a new instance of {@link GenericRequestSession} using the current configuration.
         *
         * @return A new instance of {@link GenericRequestSession}.
         * @throws BuilderException If there´s an error in the process.
         */
        public GenericRequestSession<T> build() throws BuilderException {
            URL urlObject;
            GenericRequestSession<T> genericRequestSession = null;

            try {
                urlObject = new URL(this.url);
            } catch (MalformedURLException e) {
                throw new BuilderException("There is a problem with the provided url: " + this.url);
            }

            try {
                genericRequestSession = new GenericRequestSession<>((HttpURLConnection) urlObject.openConnection());

                genericRequestSession.setTypeClass(this.typeClass);
                genericRequestSession.setRequestMethod(this.requestMethod);
                genericRequestSession.setResponseCode(this.responseCode);
                genericRequestSession.setDoOutput(this.doOutput);
                genericRequestSession.setRequestProperties(this.requestProperties);

            } catch (IOException e) {
                Logger.getLogger(Builder.class.getName()).log(Level.SEVERE, "There was an error", e);
            }

            return genericRequestSession;
        }
    }

    private final HttpURLConnection connection;

    private Class<T> typeClass;
    private Integer responseCode;

    private GenericRequestSession(HttpURLConnection connection) {
        this.connection = connection;
    }

    private void setTypeClass(Class<T> typeClass) {
        this.typeClass = typeClass;
    }

    private void setRequestMethod(RequestMethod requestMethod) {
        try {
            connection.setRequestMethod(requestMethod.toString());
        } catch (ProtocolException e) {
            logError(e);
        }
    }

    private void setResponseCode(Integer responseCode) {
        this.responseCode = responseCode;
    }

    private void setDoOutput(boolean doOutput) {
        connection.setDoOutput(doOutput);
    }

    private void setRequestProperties(Map<String, String> properties) {
        properties.keySet().forEach(key -> connection.setRequestProperty(key, properties.get(key)));
    }

    private void logError(Throwable error) {
        Logger.getLogger(GenericRequestSession.class.getName()).log(Level.SEVERE, "There was an error", error);
    }

    /**
     * Sends a basic request using the configuration of the actual {@link GenericRequestSession} instance.
     *
     * @return The response data casted to the configured type.
     * @throws ResponseException If theres an error with the request.
     */
    public T sendRequest() throws ResponseException {
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
     * Sends a request with a body using the configuration of the actual {@link GenericRequestSession} instance.
     *
     * @return The response data casted to the configured type.
     * @throws ResponseException If theres an error with the request.
     */
    public T sendRequestWithArgs(Object input) throws ResponseException {
        BufferedReader response;

        try {
            String jsonInput = new Gson().toJson(input);
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
}
