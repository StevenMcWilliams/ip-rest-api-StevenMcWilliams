package com.trillion.ip_rest_api.exception;

/**
 * An exception thrown when a specified IpAddress is unexpectedly not found in the DB.
 */
public class IpAddressNotFoundException extends Exception {
    /**
     * Constructor.
     * 
     * @param message Description of the exception.
     */
    public IpAddressNotFoundException(String message) {
        super(message);
    }
}
