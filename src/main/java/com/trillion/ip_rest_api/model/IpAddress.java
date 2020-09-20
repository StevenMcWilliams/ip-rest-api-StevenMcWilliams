package com.trillion.ip_rest_api.model;

import java.net.UnknownHostException;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.trillion.ip_rest_api.util.NetUtils;

/**
 * Persistent representation of an IPv4 address.
 */
@Entity
public class IpAddress {
    /**
     * Long value of address that uniquely identifies this instance.  Stored as a long rather than a dotted decimal
     * to facilitate querying of ranges, and sorting query results.
     */
    @Id
    private long address;
   
    /**
     * Flags whether the address has been acquired.
     */
    private boolean acquired;
    
    /**
     * Do nothing constructor used by JPA.
     */
    public IpAddress() { }
    
    /**
     * Constructor.
     * 
     * @param address Sets our address attribute.
     * @param acquired Sets our acquired attribute.
     * @throws UnknownHostException Thrown if address is invalid.
     */
    public IpAddress(long address, boolean acquired) throws UnknownHostException {
        NetUtils.assertValidLong(address);
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
        IpAddress ipAddress = (IpAddress)obj;
        if (! Objects.equals(address, ipAddress.address) || (acquired != ipAddress.acquired)) {
            return false;
        }
        return true;
    }

    /**
     * @return Returns our address attribute.
     */
    public long getAddress() {
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
    
    /**
     * @param acquired Sets our acquired attribute.
     */
    public void setAcquired(boolean acquired) {
        this.acquired = acquired;
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
