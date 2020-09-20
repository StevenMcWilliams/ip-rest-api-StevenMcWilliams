package com.trillion.ip_rest_api.dto;

import java.net.UnknownHostException;
import java.util.Objects;

import com.trillion.ip_rest_api.model.IpAddress;
import com.trillion.ip_rest_api.util.NetUtils;

/**
 * DTO representation of IpAddress.  Uses a dotted decimal rather than a long to represent the address value.
 */
public class IpAddressDTO {
    /**
     * Dotted decimal value of address that uniquely identifies this instance.
     */
    private String address;
   
    /**
     * Flags whether the address has been acquired.
     */
    private boolean acquired;
    
    /**
     * Do nothing constructor used by Jackson.
     */
    public IpAddressDTO() { }
    
    /**
     * Constructor.
     * 
     * @param ipAddress IpAddress instance to create the DTO for.
     * @throws UnknownHostException Thrown if we are unable to convert the long address to dotted decimal.
     */
    public IpAddressDTO(IpAddress ipAddress) throws UnknownHostException {
        Objects.requireNonNull(ipAddress, "ipAddress cannot be null");
        this.address = NetUtils.convertLongToDottedDecimal(ipAddress.getAddress());
        this.acquired = ipAddress.isAcquired();
    }
    
    /**
     * Constructor.
     * 
     * @param address Sets our address attribute.
     * @param acquired Sets our acquired attribute.
     */
    public IpAddressDTO(String address, boolean acquired) {
        this.address = address;
        this.acquired = acquired;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (! getClass().equals(obj.getClass())) {
            return false;
        }
        IpAddressDTO ipAddressDTO = (IpAddressDTO)obj;
        if (! Objects.equals(address, ipAddressDTO.address) || (acquired != ipAddressDTO.acquired)) {
            return false;
        }
        return true;
    }

    /**
     * @return Returns our address attribute.
     */
    public String getAddress() {
        return address;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(address, acquired);
    }

    /**
     * @return Returns our acquired attribute.
     */
    public boolean isAcquired() {
        return acquired;
    }    
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(64);
        builder.append('{');
        builder.append("address=").append(address).append(", ");
        builder.append("acquired=").append(acquired);
        builder.append('}');
        return builder.toString();
    }
}
