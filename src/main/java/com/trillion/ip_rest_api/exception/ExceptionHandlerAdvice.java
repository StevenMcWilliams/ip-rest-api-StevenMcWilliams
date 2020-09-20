package com.trillion.ip_rest_api.exception;

import java.net.UnknownHostException;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Defines how exceptions that bubble out of our controllers should be handled before returning them to REST callers.
 */
@ControllerAdvice
public class ExceptionHandlerAdvice {
    /**
     * Logger specific to this class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionHandlerAdvice.class);
    
    /**
     * Handles IpAddressNotFoundExceptions by sending back a NOT_FOUND status, with an ExceptionResponse instance in 
     * the response body.
     * 
     * @param ex IpAddressNotFoundException in question.
     * @param req HTTP request in question.
     * @return ExceptionResponse instance encapsulating info about the exception safe to return to REST callers.
     */
    @ExceptionHandler(IpAddressNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public @ResponseBody ExceptionResponse handleIpAddressNotFound(IpAddressNotFoundException ex, 
        HttpServletRequest req) 
    {
        logException(ex, req);
        return new ExceptionResponse(ex.getMessage(), req.getRequestURI());
    }

    /**
     * Handles IpAddressRangeOverlapExceptions by sending back a CONFLICT status, with an ExceptionResponse instance in 
     * the response body.
     * 
     * @param ex IpAddressRangeOverlapException in question.
     * @param req HTTP request in question.
     * @return ExceptionResponse instance encapsulating info about the exception safe to return to REST callers.
     */
    @ExceptionHandler(IpAddressOverlapException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public @ResponseBody ExceptionResponse handleIpAddressRangeOverlap(IpAddressOverlapException ex, 
        HttpServletRequest req) 
    {
        logException(ex, req);
        return new ExceptionResponse(ex.getMessage(), req.getRequestURI());
    }

    /**
     * Handles UnknownHostExceptions by sending back a BAD_REQUEST status, with an ExceptionResponse instance in the
     * response body.
     * 
     * @param ex UnknownHostException in question.
     * @param req HTTP request in question.
     * @return ExceptionResponse instance encapsulating info about the exception safe to return to REST callers.
     */
    @ExceptionHandler(UnknownHostException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody ExceptionResponse handleUnknownHost(UnknownHostException ex, HttpServletRequest req) {
        logException(ex, req);
        return new ExceptionResponse(ex.getMessage(), req.getRequestURI());
    }

    /**
     * Handles all other Exceptions by sending back an INTERNAL_SERVER_ERROR status, with an ExceptionResponse instance 
     * in the response body.
     * 
     * @param ex Exception in question.
     * @param req HTTP request in question.
     * @return ExceptionResponse instance encapsulating info about the exception safe to return to REST callers.
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public @ResponseBody ExceptionResponse handleException(Exception ex, HttpServletRequest req) {
        logException(ex, req);
        return new ExceptionResponse(ex.getMessage(), req.getRequestURI());
    }
    
    /**
     * Internal utility to output the exception stack trace to our log file.
     * 
     * @param ex Exception in question.
     * @param req HTTP request in question.
     */
    private void logException(Exception ex, HttpServletRequest req) {
        LOGGER.error("handling {} exception for request URI {}", ex.getClass().getSimpleName(), req.getRequestURI(), 
            ex);
    }
}