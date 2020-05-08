package com.dnieln7.java.generic.request;

import com.dnieln7.java.generic.request.response.DeleteResponse;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Generic class to perform HTTP requests
 *
 * @author dnieln7
 * @param <T> Class type
 */
public class HttpSession<T> {
    protected URL url;
    protected HttpURLConnection connection;

    public HttpSession(String URL) {
        try {
            this.url = new URL(URL);
        }
        catch (MalformedURLException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, e);
        }
    }

    /**
     * Get all entities
     *
     * @param typeClass Class type, e.g., Person[].class
     * @return A list of entities of the specified type
     */
    public List<T> get(Class<T[]> typeClass) {
        try {
            connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");

            if (connection.getResponseCode() != 200) {
                throw new RuntimeException("Failed: Http error code: " + connection.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            return Arrays.asList(new Gson().fromJson(br.readLine(), typeClass));
        }
        catch (IOException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, e);
        }
        finally {
            connection.disconnect();
        }

        return new ArrayList<>();
    }

    /**
     * Looks for an entity by it's identifier
     *
     * @param id Identifier
     * @param typeClass Class type, e.g., Person.class
     * @return An entity of the specified type
     */
    public T getById(String id, Class<T> typeClass) {
        try {
            connection = (HttpURLConnection) new URL(url.toString() + "/" + id).openConnection();

            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");

            if (connection.getResponseCode() != 200) {
                throw new RuntimeException("Failed: Http error code: " + connection.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            return new Gson().fromJson(br.readLine(), typeClass);
        }
        catch (IOException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, e);
        }
        finally {
            connection.disconnect();
        }

        return null;
    }

    /**
     * Saves an entity.
     *
     * @param entity Entity to be saved
     * @param typeClass Class type, e.g., Person.class
     * @return The entity that was saved; null otherwise
     */
    public T post(T entity, Class<T> typeClass) {
        try {
            connection = (HttpURLConnection) url.openConnection();

            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");

            String input = new Gson().toJson(entity);

            OutputStream os = connection.getOutputStream();

            os.write(input.getBytes());

            os.flush();

            if (connection.getResponseCode() != 200) {
                throw new RuntimeException("Failed: Http error code: " + connection.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            return new Gson().fromJson(br.readLine(), typeClass);
        }
        catch (IOException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, e);
        }
        finally {
            connection.disconnect();
        }

        return null;
    }

    /**
     * Updates an entity.
     *
     * @param entity Entity to be updated
     * @param id Identifier
     * @param typeClass Class type, e.g., Person.class
     * @return The entity that was updated; null otherwise
     */
    public T put(String id, T entity, Class<T> typeClass) {
        try {
            connection = (HttpURLConnection) new URL(url.toString() + "/" + id).openConnection();

            connection.setDoOutput(true);
            connection.setRequestMethod("PUT");
            connection.setRequestProperty("Content-Type", "application/json");

            String input = new Gson().toJson(entity);

            OutputStream os = connection.getOutputStream();

            os.write(input.getBytes());

            os.flush();

            if (connection.getResponseCode() != 200) {
                throw new RuntimeException("Failed: Http error code: " + connection.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            return new Gson().fromJson(br.readLine(), typeClass);
        }
        catch (IOException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, e);
        }
        finally {
            connection.disconnect();
        }

        return null;
    }

    /**
     * Deletes an entity by it's identifier.
     *
     * @param id Identifier.
     * @return {@link DeleteResponse}
     */
    public DeleteResponse delete(String id) {
        try {
            connection = (HttpURLConnection) new URL(url.toString() + "/" + id).openConnection();

            connection.setRequestMethod("DELETE");
            connection.setRequestProperty("Content-Type", "application/json");

            if (connection.getResponseCode() != 200) {
                throw new RuntimeException("Failed: Http error code: " + connection.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            return new Gson().fromJson(br.readLine(), DeleteResponse.class);
        }
        catch (IOException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, e);
        }
        finally {
            connection.disconnect();
        }

        return null;
    }
}
