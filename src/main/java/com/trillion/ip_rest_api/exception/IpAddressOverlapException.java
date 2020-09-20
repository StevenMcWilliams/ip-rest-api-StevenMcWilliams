package com.trillion.ip_rest_api.exception;

/**
 * An exception thrown when a specified CIDR block overlaps unexpectedly with one or more existing IpAddress instances.
 */
public class IpAddressOverlapException extends Exception {
    /**
     * Constructor.
     * 
     * @param message Description of the exception.
     */
    public IpAddressOverlapException(String message) {
        super(message);
    }
}
