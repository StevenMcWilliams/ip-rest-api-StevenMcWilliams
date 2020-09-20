package com.trillion.ip_rest_api.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.UnknownHostException;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.trillion.ip_rest_api.model.IpAddress;

/**
 * TDD style unit tests for IpAddressDTOTest.
 * <p>
 * Note that I am not bothering to test simple setters/getters/hashCode methods here.
 */
@Tag("dto")
public class IpAddressDTOTest {
    
    /* -------- tests for constructor method -------- */

    /**
     * Verifies that the ctor takes a null IpAddress as input and expects a NullPointerException.
     * 
     * @throws UnknownHostException Should not happen.
     */
    @Test
    public void ctor_null_throwsNullPointer() throws UnknownHostException {
        assertThrows(NullPointerException.class, () -> {
            new IpAddressDTO(null);
        });
    }
    
    /**
     * Verifies that the ctor takes a non-null IpAddress as input and uses it to populate the DTO, while converting the
     * long address to a dotted decimal address.
     * 
     * @throws UnknownHostException Should not happen.
     */
    @Test
    public void ctor_nonNull_convertsAddress() throws UnknownHostException {
        // setup test
        IpAddress ipAddress = new IpAddress(16_777_216L, true);

        // execute test
        IpAddressDTO result = new IpAddressDTO(ipAddress);
        
        // verify result
        assertEquals("1.0.0.0", result.getAddress());
        assertTrue(result.isAcquired());
    }
    
    /* -------- tests for equals method -------- */

    /**
     * Verifies that equals method returns false if target ipAddressDTO is null.
     */
    @Test
    public void equals_null_returnsFalse() {
        // setup for test
        IpAddressDTO src = new IpAddressDTO("1.0.0.0", false);
        IpAddressDTO tgt = null;

        // execute test method
        boolean result = src.equals(tgt);

        // verify result
        assertFalse(result);
    }

    /**
     * Verifies that equals method returns false if target is not a ipAddressDTO.
     */
    @Test
    public void equals_alienType_returnsFalse() {
        // setup for test
        IpAddressDTO src = new IpAddressDTO("1.0.0.0", false);
        Object tgt = "tgt";

        // execute test method
        boolean result = src.equals(tgt);

        // verify result
        assertFalse(result);
    }

    /**
     * Verifies that equals method returns false if ipAddressDTO addresses are different.
     */
    @Test
    public void equals_differentAddresses_returnsFalse() {
        // setup for test
        IpAddressDTO src = new IpAddressDTO("1.0.0.0", false);
        IpAddressDTO tgt = new IpAddressDTO("2.0.0.0", false);

        // execute test method
        boolean result = src.equals(tgt);

        // verify result
        assertFalse(result);
    }

    /**
     * Verifies that equals method returns false if ipAddressDTO acquired flags are different.
     */
    @Test
    public void equals_differentAcquiredFlags_returnsFalse() {
        // setup for test
        IpAddressDTO src = new IpAddressDTO("1.0.0.0", false);
        IpAddressDTO tgt = new IpAddressDTO("1.0.0.0", true);

        // execute test method
        boolean result = src.equals(tgt);

        // verify result
        assertFalse(result);
    }
    
    /**
     * Verifies that equals method returns true if both ipAddressDTOs are equal.
     */
    @Test
    public void equals_equal_returnsTrue() {
        // setup for test
        IpAddressDTO src = new IpAddressDTO("1.0.0.0", false);
        IpAddressDTO tgt = new IpAddressDTO("1.0.0.0", false);

        // execute test method
        boolean result = src.equals(tgt);

        // verify result
        assertTrue(result);
    }
}
