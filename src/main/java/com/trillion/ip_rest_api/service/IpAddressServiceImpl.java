package com.trillion.ip_rest_api.service;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.net.util.SubnetUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.trillion.ip_rest_api.IpRestApiApplication;
import com.trillion.ip_rest_api.exception.IpAddressNotFoundException;
import com.trillion.ip_rest_api.exception.IpAddressOverlapException;
import com.trillion.ip_rest_api.model.IpAddress;
import com.trillion.ip_rest_api.repository.IpAddressRepository;
import com.trillion.ip_rest_api.util.NetUtils;
 
/**
 * Provides IpAddress related service operations.
 */
@Service
public class IpAddressServiceImpl implements IpAddressService {
    /**
     * Loads entries from application.properties.
     */
    @Autowired
    Environment env;
    
    /**
     * Repository to use to persist IpAddress instances.
     */
    @Autowired
    private IpAddressRepository repository;

    @Override
    public IpAddress acquire(String address) throws IpAddressNotFoundException, UnknownHostException {
        Objects.requireNonNull(address, "address cannot be null");
        
        // fetch existing IpAddress
        Optional<IpAddress> addressOpt = getById(address);
        if (addressOpt.isEmpty()) {
            throw new IpAddressNotFoundException("address " + address + " not found");
        }

        // update IpAddress in DB to set acquired flag true, if not already true
        IpAddress ipAddress = addressOpt.get();
        if (! ipAddress.isAcquired()) {       
            ipAddress.setAcquired(true);           
            return save(ipAddress);
        } else {
            return ipAddress;
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * The IpAddress instances are added in batches, according to the JDBC batch size configuration from the
     * application.properties file.  This is done in order to avoid having to hold potentially large numbers of these 
     * instances in memory concurrently.  
     * <p>
     * Note that each of the DB operations here, namely saving each batch of new IpAddress instances, is executed in a 
     * separate transaction, in order to avoid potentially large DB transactions.  This means however that this method 
     * is not atomic.
     */
    @Override
    public long addBlock(String networkAddress, int cidrMask) throws UnknownHostException, IpAddressOverlapException {
        Objects.requireNonNull(networkAddress, "networkAddress cannot be null");
        
        String cidrBlock = networkAddress + "/" + cidrMask;
        
        // get start and end addresses in the block
        SubnetUtils.SubnetInfo subnetInfo = NetUtils.getSubnetInfo(cidrBlock);
        String startAddressDottedDecimal = subnetInfo.getLowAddress();
        String endAddressDottedDecimal = subnetInfo.getHighAddress();
        long startAddress = NetUtils.convertDottedDecimalToLong(startAddressDottedDecimal);
        long endAddress = NetUtils.convertDottedDecimalToLong(endAddressDottedDecimal);
        
        // make sure proposed block doesn't overlap any existing IpAddresses
        if (existsWithinRange(startAddress, endAddress)) {
            throw new IpAddressOverlapException("CIDR block " + cidrBlock + " overlaps existing addresses");
        }

        // get batchSize value from application.properties (default to 1)
        String batchSizeProp = env.getProperty(IpRestApiApplication.JDBC_BATCH_SIZE, "1");
        int batchSize = Integer.valueOf(batchSizeProp);

        // save new IpAddress instances to DB (in batches for efficiency)
        List<IpAddress> ipAddresses = new ArrayList<>();
        long numAddresses = endAddress - startAddress + 1;
        long i = 1L;
        for (long address = startAddress; address <= endAddress; address++) {
            IpAddress ipAddress = new IpAddress(address, false);
            ipAddresses.add(ipAddress);
            if (((i % batchSize) == 0) || (i == numAddresses)) {
                saveAll(ipAddresses);
                ipAddresses.clear();
            }
            i++;
        }

        return numAddresses;
    }

    @Override
    public void deleteAll() {
        repository.deleteAll();
    }

    @Override
    public boolean existsWithinRange(long startAddress, long endAddress) {
        return repository.existsWithinRange(startAddress, endAddress);
    }

    @Override
    public List<IpAddress> getAll() {        
        List<IpAddress> addresses = new ArrayList<>();
        repository.findAll().forEach(address -> addresses.add(address));
        return addresses;
    }

    @Override
    public Optional<IpAddress> getById(String address) throws UnknownHostException {
        Objects.requireNonNull(address, "address cannot be null");
        long longAddress = NetUtils.convertDottedDecimalToLong(address);
        return repository.findById(longAddress);
    }

    @Override
    public long getCount() {
        return repository.count();
    }

    @Override
    public List<IpAddress> getPage(int pageNum, int pageSize) {        
        Pageable pageable = PageRequest.of(pageNum, pageSize);
        Page<IpAddress> page = repository.findAll(pageable);
        return page.toList();
    }

    @Override
    public IpAddress release(String address) throws IpAddressNotFoundException, UnknownHostException {
        Objects.requireNonNull(address, "address cannot be null");

        // fetch existing IpAddress
        Optional<IpAddress> addressOpt = getById(address);
        if (addressOpt.isEmpty()) {
            throw new IpAddressNotFoundException("address " + address + " not found");
        }

        // update IpAddress in DB to set acquired flag false, if not already false
        IpAddress ipAddress = addressOpt.get();
        if (ipAddress.isAcquired()) {       
            ipAddress.setAcquired(false);
            return save(ipAddress);
        } else {
            return ipAddress;
        }
    }

    @Override
    public IpAddress save(IpAddress ipAddress) {
        Objects.requireNonNull(ipAddress, "ipAddress cannot be null");
        return repository.save(ipAddress);
    }    

    @Override
    public void saveAll(List<IpAddress> ipAddresses) {
        Objects.requireNonNull(ipAddresses, "ipAddresses cannot be null");
        repository.saveAll(ipAddresses);
    }    
}
