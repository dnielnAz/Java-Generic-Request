package com.dnieln7.java.generic.request.exception;

import com.dnieln7.java.generic.request.GenericRequestSession;

/**
 * Exception thrown if theres an error in the {@link GenericRequestSession} building process.
 *
 * @author dnieln7
 */
public class BuilderException extends Exception {
    public BuilderException(String message) {
        super(message);
    }
}
