package com.trillion.ip_rest_api.model;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.UnknownHostException;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.trillion.ip_rest_api.util.NetUtils;

/**
 * TDD style unit tests for IpAddressTest.
 * <p>
 * Note that I am not bothering to test simple setters/getters here.
 */
@Tag("model")
public class IpAddressTest {
    
    /* -------- tests for constructor method -------- */

    /**
     * Verifies that the ctor throws an UnknownHostException if an invalid address is passed in.
     * 
     * @param address Address to test.
     */
    @ParameterizedTest
    @ValueSource(longs = {
        -1L,
        NetUtils.MAX_LONG_ADDRESS + 1
    })
    public void ctor_invalid_throwsUnknownHost(long address) {
        assertThrows(UnknownHostException.class, () -> {
            new IpAddress(address, false);
        });
    }
    

    /**
     * Verifies that the ctor does not throw any exception if a valid address is passed in.
     * 
     * @param address Address to test.
     * @throws UnknownHostException Should not happen.
     */
    @ParameterizedTest
    @ValueSource(longs = {
        0L,
        NetUtils.MAX_LONG_ADDRESS 
    })
    public void ctor_valid_returns(long address) throws UnknownHostException {
        new IpAddress(address, false);
    }    

    /* -------- tests for equals method -------- */

    /**
     * Verifies that equals method returns false if target ipAddressDTO is null.
     * 
     * @throws UnknownHostException Should not happen.
     */
    @Test
    public void equals_null_returnsFalse() throws UnknownHostException {
        // setup for test
        IpAddress src = new IpAddress(16_777_216L, false);
        IpAddress tgt = null;

        // execute test method
        boolean result = src.equals(tgt);

        // validate result
        assertFalse(result);
    }

    /**
     * Verifies that equals method returns false if target is not a ipAddressDTO.
     * 
     * @throws UnknownHostException Should not happen.
     */
    @Test
    public void equals_alienType_returnsFalse() throws UnknownHostException {
        // setup for test
        IpAddress src = new IpAddress(16_777_216L, false);
        Object tgt = "tgt";

        // execute test method
        boolean result = src.equals(tgt);

        // validate result
        assertFalse(result);
    }

    /**
     * Verifies that equals method returns false if ipAddressDTO addresses are different.
     * 
     * @throws UnknownHostException Should not happen.
     */
    @Test
    public void equals_differentAddresses_returnsFalse() throws UnknownHostException {
        // setup for test
        IpAddress src = new IpAddress(16_777_216L, false);
        IpAddress tgt = new IpAddress(33_554_432L, false);

        // execute test method
        boolean result = src.equals(tgt);

        // validate result
        assertFalse(result);
    }

    /**
     * Verifies that equals method returns false if ipAddressDTO acquired flags are different.
     * 
     * @throws UnknownHostException Should not happen.
     */
    @Test
    public void equals_differentAcquiredFlags_returnsFalse() throws UnknownHostException {
        // setup for test
        IpAddress src = new IpAddress(16_777_216L, false);
        IpAddress tgt = new IpAddress(16_777_216L, true);

        // execute test method
        boolean result = src.equals(tgt);

        // validate result
        assertFalse(result);
    }

    /**
     * Verifies that equals method returns true if both ipAddresss are equal.
     * 
     * @throws UnknownHostException Should not happen.
     */
    @Test
    public void equals_equal_returnsTrue() throws UnknownHostException {
        // setup for test
        IpAddress src = new IpAddress(16_777_216L, false);
        IpAddress tgt = new IpAddress(16_777_216L, false);

        // execute test method
        boolean result = src.equals(tgt);

        // validate result
        assertTrue(result);
    }
}
