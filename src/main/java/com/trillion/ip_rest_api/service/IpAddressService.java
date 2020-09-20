package com.trillion.ip_rest_api.service;

import java.net.UnknownHostException;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.trillion.ip_rest_api.exception.IpAddressNotFoundException;
import com.trillion.ip_rest_api.exception.IpAddressOverlapException;
import com.trillion.ip_rest_api.model.IpAddress;
 
/**
 * Interface for IpAddress related service operations.
 */
@Service
public interface IpAddressService {
    /**
     * Updates a specified IpAddress instance to set its acquired flag to true (if it is not already).
     * 
     * @param address Dotted decimal address to update.
     * @return IpAddress updated.
     * @throws IpAddressNotFoundException Thrown if address does not exist yet in DB.
     * @throws UnknownHostException Thrown if unable to parse input address.
     */
    IpAddress acquire(String address) throws IpAddressNotFoundException, UnknownHostException; 

    /**
     * Adds new IpAddress instances within (inclusive) the specified CIDR block.  
     *
     * @param networkAddress Dotted decimal address of CIDR block being added (e.g. "1.0.0.0").
     * @param cidrMask CIDR mask of the block being added (e.g. 24).
     * @return Count of instances that were added.
     * @throws UnknownHostException Thrown if unable to translate input address successfully.
     * @throws IpAddressOverlapException Thrown if CIDR block overlaps one or more existing ones already in DB.
     */
    long addBlock(String networkAddress, int cidrMask) throws UnknownHostException, IpAddressOverlapException;

    /**
     * Deletes all IpAddress instances.
     */
    void deleteAll(); 
    
    /**
     * Query to test whether any IpAddress instances exist in the specified range (inclusive).
     * 
     * @param startAddress Start address of the range.
     * @param endAddress End address of the range.
     * @return Returns true if any IpAddress instance existing in the range.
     */
    boolean existsWithinRange(long startAddress, long endAddress);

    /**
     * Fetches all IpAddress instances present, in ascending order.
     * 
     * @return List of IpAddresses fetched.
     */
    List<IpAddress> getAll();         
    
    /**
     * Gets specified IpAddress instance by its primary key (address).
     * 
     * @param address Dotted decimal address to fetch.
     * @return IpAddress fetched (if any).
     * @throws UnknownHostException Thrown if unable to parse input address.
     */
    Optional<IpAddress> getById(String address) throws UnknownHostException; 
    
    /**
     * Fetches count of all IpAddress instances present.
     *
     * @return Count of IpAddresses present.
     */
    long getCount();

    /**
     * Fetches one page of IpAddress instances, in ascending order.
     * 
     * @param pageNum Page number to return from the result set.
     * @param pageSize Number of entries which constitute a page.
     * @return List of IpAddresses fetched from that page.
     */
    List<IpAddress> getPage(int pageNum, int pageSize);        
    
    /**
     * Updates a specified IpAddress instance to set its acquired flag to false (if it is not already).
     * 
     * @param address Dotted decimal address to update.
     * @return IpAddress updated.
     * @throws IpAddressNotFoundException Thrown if address does not exist yet in DB.
     * @throws UnknownHostException Thrown if unable to parse input address.
     */
    IpAddress release(String address) throws IpAddressNotFoundException, UnknownHostException;
    
    /**
     * Adds or updates IpAddress instance to the DB. 
     * 
     * @param ipAddress IpAddress instance to save.
     * @return IpAddress instance added or updated.
     */
    IpAddress save(IpAddress ipAddress);
    
    /**
     * Adds or updates IpAddress instance to the DB. 
     * 
     * @param ipAddresses List of IpAddress instances to save.
     */
    void saveAll(List<IpAddress> ipAddresses);
}
