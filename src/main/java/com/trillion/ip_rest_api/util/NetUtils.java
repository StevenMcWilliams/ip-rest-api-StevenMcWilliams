package com.trillion.ip_rest_api.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Objects;

import org.apache.commons.net.util.SubnetUtils;
import org.apache.commons.validator.routines.InetAddressValidator;

/**
 * Encapsulates network related utility methods.
 */
public class NetUtils {
    /**
     * Max allowed value for an IPv4 address (2^32 - 1).
     */
    public final static long MAX_LONG_ADDRESS = 4_294_967_295L;
    
    /**
     * Asserts that the specified dotted decimal string address is a valid IPv4 address.
     * 
     * @param address Dotted decimal address to test.
     * @throws UnknownHostException Thrown if address is invalid.
     */
    public static void assertValidDottedDecimal(String address) throws UnknownHostException {
        Objects.requireNonNull(address, "address must not be null");

        if (! InetAddressValidator.getInstance().isValidInet4Address(address)) {
            throw new UnknownHostException(address + " is not a valid IPv4 dotted decimal address");
        }        
    }
    
    /**
     * Asserts that the specified long address is a valid IPv4 address.
     * 
     * @param address Long address to test.
     * @throws UnknownHostException Thrown if address is invalid.
     */
    public static void assertValidLong(long address) throws UnknownHostException {
        if ((address < 0) || (address > MAX_LONG_ADDRESS)) {
            throw new UnknownHostException(address + " is not a valid IPv4 long address");
        }
    }
    
    /**
     * Converts the given IPv4 address from a dotted decimal string a long.
     *
     * @param dottedDecimalAddress Dotted decimal string address to convert.
     * @return Long version of input address.
     * @throws UnknownHostException Thrown if unable to parse input address successfully.
     */
    public static long convertDottedDecimalToLong(String dottedDecimalAddress) throws UnknownHostException {
        assertValidDottedDecimal(dottedDecimalAddress);
        
        InetAddress inetAddress = InetAddress.getByName(dottedDecimalAddress);

        byte[] bytes = inetAddress.getAddress();

        long address = bytes[3] & 0xFF;
        address |= ((((long)bytes[2] & 0xFF) << 8) & 0xFF00);
        address |= ((((long)bytes[1] & 0xFF) << 16) & 0xFF0000);
        address |= ((((long)bytes[0] & 0xFF) << 24) & 0xFF000000);

        return address;
    }
    
    /**
     * Converts the given IPv4 address from a long to a dotted decimal string.
     *
     * @param longAddress Dotted decimal string address to convert.
     * @return Dotted decimal version of input address.
     * @throws UnknownHostException Thrown if unable to parse input address successfully.
     */
    public static String convertLongToDottedDecimal(long longAddress) throws UnknownHostException {
        assertValidLong(longAddress);

        byte[] bytes = new byte[4];
        bytes[3] = (byte)(longAddress & 0xFF);
        bytes[2] = (byte)((longAddress & 0xFF00) >> 8);
        bytes[1] = (byte)((longAddress & 0xFF0000) >> 16);
        bytes[0] = (byte)((longAddress & 0xFF000000) >> 24);        
        
        InetAddress inetAddress = InetAddress.getByAddress(bytes);
        
        return inetAddress.getHostAddress();
    }
    
    /**
     * Returns SubnetInfo about a specified CIDR block, including start and end addresses as part of the subnet.
     * 
     * @param cidrBlock CIDR block in question (e.g. "1.0.0.0/24").
     * @return SubnetInfo for CIDR block.
     * @throws UnknownHostException Thrown if unable to parse CIDR block.
     */
    public static SubnetUtils.SubnetInfo getSubnetInfo(String cidrBlock) throws UnknownHostException {
        Objects.requireNonNull(cidrBlock, "cidrBlock cannot be null");
        
        try {
            SubnetUtils subnetUtils = new SubnetUtils(cidrBlock);
            subnetUtils.setInclusiveHostCount(true);
            return subnetUtils.getInfo();
        } catch (IllegalArgumentException ex) {
            throw new UnknownHostException(ex.getMessage());
        }
    }
}
