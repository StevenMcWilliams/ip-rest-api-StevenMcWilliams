package com.trillion.ip_rest_api.exception;

/**
 * Class to encapsulate information about an exception which is safe to return to REST callers.
 */
public class ExceptionResponse {
    /**
     * Error message (if any) from the original exception.
     */
    private final String errorMessage;

    /**
     * URI of the original REST request.
     */
    private final String requestedURI;
    
    /**
     * Constructor.
     * 
     * @param errorMessage Sets our errorMessage attribute.
     * @param requestedURI Sets our requestedURI attribute.
     */
    public ExceptionResponse(String errorMessage, String requestedURI) {
        this.errorMessage = errorMessage;
        this.requestedURI = requestedURI;
    }

    /**
     * @return Returns our errorMessage attribute.
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * @return Returns our requestedURI attribute.
     */
    public String getRequestedURI() {
        return requestedURI;
    }
}