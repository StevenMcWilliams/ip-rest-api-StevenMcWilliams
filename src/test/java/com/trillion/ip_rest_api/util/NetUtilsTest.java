package com.trillion.ip_rest_api.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.net.UnknownHostException;

import org.apache.commons.net.util.SubnetUtils;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * TDD style unit tests for NetUtils.
 */
@Tag("utils")
public class NetUtilsTest {
    
    /* -------- tests for assertValidDottedDecimal method -------- */

    /**
     * Verifies that the method throws a NullPointerException if a null address is passed in.
     * 
     * @throws UnknownHostException Should not happen.
     */
    @Test
    public void assertValidDottedDecimal_null_throwsNullPoiter() throws UnknownHostException {
        assertThrows(NullPointerException.class, () -> {
            NetUtils.assertValidDottedDecimal(null);
        });        
    }
    
    /**
     * Verifies that the method throws an UnknownHostException if an invalid address is passed in.
     * 
     * @param address Address to test.
     */
    @ParameterizedTest
    @ValueSource(strings = {
        "",
        "1.0.0",
        "1.0.0.0.0",
        "-1.0.0.0",
        "256.0.0.0"
    })
    public void assertValidDottedDecimal_invalid_throwsUnknownHost(String address) {
        assertThrows(UnknownHostException.class, () -> {
            NetUtils.assertValidDottedDecimal(address);
        });
    }
    
    /**
     * Verifies that the method returns if a valid address is passed in.
     * 
     * @param address Address to test.
     * @throws UnknownHostException Should not happen.
     */
    @ParameterizedTest
    @ValueSource(strings = {
        "0.0.0.0",
        "255.255.255.255"
    })
    public void assertValidDottedDecimal_valid_returns(String address) throws UnknownHostException {
        NetUtils.assertValidDottedDecimal(address);
    }    

    /* -------- tests for assertValidLong method -------- */

    /**
     * Verifies that the method throws an UnknownHostException if an invalid address is passed in.
     * 
     * @param address Address to test.
     */
    @ParameterizedTest
    @ValueSource(longs = {
        -1L,
        NetUtils.MAX_LONG_ADDRESS + 1
    })
    public void assertValidLong_invalid_throwsUnknownHost(long address) {
        assertThrows(UnknownHostException.class, () -> {
            NetUtils.assertValidLong(address);
        });
    }    

    /**
     * Verifies that the method does not throw any exception if a valid address is passed in.
     * 
     * @param address Address to test.
     * @throws UnknownHostException Should not happen.
     */
    @ParameterizedTest
    @ValueSource(longs = {
        0L,
        NetUtils.MAX_LONG_ADDRESS 
    })
    public void assertValidLong_valid_returns(long address) throws UnknownHostException {
        NetUtils.assertValidLong(address);
    }
    
    /* -------- tests for convertDottedDecimalToLong method -------- */
    
    /**
     * Verifies that method correctly converts a min address value.
     * 
     * @throws UnknownHostException Should not happen.
     */
    @Test
    public void convertDottedDecimalToLong_min_returnsMin() throws UnknownHostException {
        long result = NetUtils.convertDottedDecimalToLong("0.0.0.0");
        
        assertEquals(0, result);
    }
    
    /**
     * Verifies that method correctly converts a max address value.
     * 
     * @throws UnknownHostException Should not happen.
     */
    @Test
    public void convertDottedDecimalToLong_max_returnsMax() throws UnknownHostException {
        long result = NetUtils.convertDottedDecimalToLong("255.255.255.255");
        
        assertEquals(NetUtils.MAX_LONG_ADDRESS, result);
    }
    
    /* -------- tests for convertLongToDottedDecimal method -------- */
    
    /**
     * Verifies that method correctly converts a min address value.
     * 
     * @throws UnknownHostException Should not happen.
     */
    @Test
    public void convertLongToDottedDecimal_min_returnsMin() throws UnknownHostException {
        String result = NetUtils.convertLongToDottedDecimal(0L);
        
        assertEquals("0.0.0.0", result);
    }
    
    /**
     * Verifies that method correctly converts a max address value.
     * 
     * @throws UnknownHostException Should not happen.
     */
    @Test
    public void convertLongToDottedDecimal_max_returnsMax() throws UnknownHostException {
        String result = NetUtils.convertLongToDottedDecimal(NetUtils.MAX_LONG_ADDRESS);
        
        assertEquals("255.255.255.255", result);
    }
    
    /* -------- tests for getSubnetInfo method -------- */

    /**
     * Verifies that the method throws a NullPointerException if a null address is passed in.
     * 
     * @throws UnknownHostException Should not happen.
     */
    @Test
    public void getSubnetInfo_null_throwsNullPointer() throws UnknownHostException {
        assertThrows(NullPointerException.class, () -> {
            NetUtils.getSubnetInfo(null);
        });
    }    

    /**
     * Verifies that the method throws an UnknownHostException if an invalid address is passed in.
     * 
     * @param cidrBlock CIDR block to test.
     */
    @ParameterizedTest
    @ValueSource(strings = {
        "",
        "1.0.0/30",
        "1.0.0.0.0/30",
        "-1.0.0.0/30",
        "256.0.0.0/30",
        "1.0.0.0/-1",
        "1.0.0.0/33"
    })
    public void getSubnetInfo_invalid_throwsUnknownHost(String cidrBlock) {
        assertThrows(UnknownHostException.class, () -> {
            NetUtils.getSubnetInfo(cidrBlock);
        });        
    }    

    /**
     * Verifies that the method throws an UnknownHostException if an invalid address is passed in.
     * 
     * @param cidrBlock CIDR block to test.
     * @throws UnknownHostException Should not happen.
     */
    @ParameterizedTest
    @ValueSource(strings = {
        "0.0.0.0/0",
        "255.255.255.255/32"
    })
    public void getSubnetInfo_valid_returnsSubnetInfo(String cidrBlock) throws UnknownHostException {
        // execute test
        SubnetUtils.SubnetInfo subnetInfo = NetUtils.getSubnetInfo(cidrBlock);
        
        // verify results
        assertNotNull(subnetInfo);
    }    
}
