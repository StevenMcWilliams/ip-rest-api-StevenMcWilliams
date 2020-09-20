package com.trillion.ip_rest_api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.UnknownHostException;
import java.util.Optional;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;

import com.trillion.ip_rest_api.IpRestApiApplication;
import com.trillion.ip_rest_api.exception.IpAddressNotFoundException;
import com.trillion.ip_rest_api.exception.IpAddressOverlapException;
import com.trillion.ip_rest_api.model.IpAddress;
import com.trillion.ip_rest_api.repository.IpAddressRepository;

/**
 * TDD style unit tests for IpAddressService.
 * <p>
 * Note that I am not bothering to test the deleteAll, getAll, getCount, or getPage methods here, due to their 
 * simplicity.
 */
@ExtendWith(MockitoExtension.class)
@Tag("service")
public class IpAddressServiceTest {
    /**
     * Mock Environment instance to use when a test runs.
     */
    @Mock
    private Environment env;
    
    /**
     * Mock IpAddressRepository instance to use when a test runs.
     */
    @Mock
    private IpAddressRepository repository;
    
    /**
     * IpAddressServiceImple instance to use when a test runs (injected with mock repository).
     */
    @InjectMocks
    private IpAddressServiceImpl service;
    
    /* -------- tests for acquire method -------- */

    /**
     * Tests method for acquiring a null address.  Expected to throw a NullPointerException.
     * 
     * @throws UnknownHostException Should not happen.
     * @throws IpAddressNotFoundException Should not happen.
     */
    @Test
    public void acquire_null_throwsNullPointer() throws UnknownHostException, IpAddressNotFoundException {
        assertThrows(NullPointerException.class, () -> {
            service.acquire(null);
        });
    }

    /**
     * Tests method for acquiring an IpAddress that does not exist.  Expected to throw an IpAddressNotFoundException.
     * 
     * @throws UnknownHostException Should not happen.
     */
    @Test
    public void acquire_doesNotExist_throwsIpAddressNotFound() throws UnknownHostException {
        // setup test
        Optional<IpAddress> ipAddressOpt = Optional.empty();
        Mockito.when(repository.findById(16_777_216L)).thenReturn(ipAddressOpt);
        
        // execute test
        assertThrows(IpAddressNotFoundException.class, () -> {
            service.acquire("1.0.0.0");
        });
    }

    /**
     * Tests method for acquiring an IpAddress that exists with acquired set false.  Expected to set the acquired flag 
     * true for it, save it to DB, and return it.
     * 
     * @throws UnknownHostException Should not happen.
     * @throws IpAddressNotFoundException Should not happen.
     */
    @Test
    public void acquire_existsNotAcquired_setsAcquireTrueAndSaves_returnsIpAddress() throws UnknownHostException, 
        IpAddressNotFoundException 
    {
        // setup test
        IpAddress ipAddress = new IpAddress(16_777_216L, false);
        Optional<IpAddress> ipAddressOpt = Optional.of(ipAddress);
        Mockito.when(repository.findById(16_777_216L)).thenReturn(ipAddressOpt);
        Mockito.when(repository.save(ipAddress)).thenReturn(ipAddress);
        
        // execute test
        IpAddress result = service.acquire("1.0.0.0");

        // verify result
        assertNotNull(result);
        assertTrue(result.isAcquired());
    }

    /**
     * Tests method for acquiring an IpAddress that exists with acquired set true.  Expected do nothing and return it.
     * 
     * @throws UnknownHostException Should not happen.
     * @throws IpAddressNotFoundException Should not happen.
     */
    @Test
    public void acquire_existsAcquired_returnsIpAddress() throws UnknownHostException, 
        IpAddressNotFoundException 
    {
        // setup test
        IpAddress ipAddress = new IpAddress(16_777_216L, true);
        Optional<IpAddress> ipAddressOpt = Optional.of(ipAddress);
        Mockito.when(repository.findById(16_777_216L)).thenReturn(ipAddressOpt);
        
        // execute test
        IpAddress result = service.acquire("1.0.0.0");

        // verify result
        assertNotNull(result);
        assertTrue(result.isAcquired());
    }
    
    /* -------- tests for addBlock method -------- */

    /**
     * Tests method for adding a null CIDR block.  Expected to throw a NullPointerException.
     * 
     * @throws UnknownHostException Should not happen.
     * @throws IpAddressNotFoundException Should not happen.
     */
    @Test
    public void addBlock_null_throwsNullPointer() throws UnknownHostException, IpAddressOverlapException {
        assertThrows(NullPointerException.class, () -> {
            service.addBlock(null, 0);
        });
    }

    /**
     * Tests method for adding an invalid CIDR block.  Expected to throw an UnknownHostException.
     * 
     * @throws IpAddressOverlapException Should not happen.
     */
    @Test
    public void addBlock_invalid_throwsUnknownHost() throws IpAddressOverlapException {
        assertThrows(UnknownHostException.class, () -> {
            service.addBlock("1.0.0", 30);
        });
    }

    /**
     * Tests method for adding a CIDR block that overlaps an existing block.  Expected to throw an
     * IpAddressOverlapException.
     * 
     * @throws UnknownHostException Should not happen.
     */
    @Test
    public void addBlock_overlap_throwsIpAddressOverlap() throws UnknownHostException {
        // setup test
        Mockito.when(repository.existsWithinRange(16_777_216L, 16_777_219L)).thenReturn(true);
        
        // execute test
        assertThrows(IpAddressOverlapException.class, () -> {
            service.addBlock("1.0.0.0", 30);
        });
    }

    /**
     * Tests method for adding a CIDR block of size 4, that does not overlap any existing blocks, and has a batch size 
     * of 256.  Expected to call saveAll once to save all IpAddresses in the range, and return a count of 4.
     * 
     * @throws UnknownHostException Should not happen.
     * @throws IpAddressOverlapException Should not happen.
     */
    @Test
    public void addBlockOf4_noOverlap_256BatchSize_1CallOnSaveAll_returns4() throws UnknownHostException, 
        IpAddressOverlapException 
    {
        // setup test
        Mockito.when(repository.existsWithinRange(16_777_216L, 16_777_219L)).thenReturn(false);
        Mockito.when(env.getProperty(IpRestApiApplication.JDBC_BATCH_SIZE, "1")).thenReturn("256");
        
        // execute test
        long result = service.addBlock("1.0.0.0", 30);

        // verify result
        assertEquals(4, result);
        Mockito.verify(repository, Mockito.times(1)).saveAll(Mockito.anyList());
    }

    /**
     * Tests method for adding a CIDR block of size 4, that does not overlap any existing blocks, and has a batch size 
     * of 1.  Expected to call saveAll 4 times, and return a count of 4.
     * 
     * @throws UnknownHostException Should not happen.
     * @throws IpAddressOverlapException Should not happen.
     */
    @Test
    public void addBlock_noOverlap_1BatchSize_4CallsOnSaveAll_returns4() throws UnknownHostException, 
        IpAddressOverlapException 
    {
        // setup test
        Mockito.when(repository.existsWithinRange(16_777_216L, 16_777_219L)).thenReturn(false);
        Mockito.when(env.getProperty(IpRestApiApplication.JDBC_BATCH_SIZE, "1")).thenReturn("1");
        
        // execute test
        long result = service.addBlock("1.0.0.0", 30);

        // verify result
        assertEquals(4, result);
        Mockito.verify(repository, Mockito.times(4)).saveAll(Mockito.anyList());
    }

    /**
     * Tests method for adding a CIDR block of size 4, that does not overlap any existing blocks, and has a batch size 
     * of 3.  Expected to call saveAll 2 times, and return a count of 4.
     * 
     * @throws UnknownHostException Should not happen.
     * @throws IpAddressOverlapException Should not happen.
     */
    @Test
    public void addBlock_noOverlap_3BatchSize_2CallsOnSaveAll_returns4() throws UnknownHostException, 
        IpAddressOverlapException 
    {
        // setup test
        Mockito.when(repository.existsWithinRange(16_777_216L, 16_777_219L)).thenReturn(false);
        Mockito.when(env.getProperty(IpRestApiApplication.JDBC_BATCH_SIZE, "1")).thenReturn("3");
        
        // execute test
        long result = service.addBlock("1.0.0.0", 30);

        // verify result
        assertEquals(4, result);
        Mockito.verify(repository, Mockito.times(2)).saveAll(Mockito.anyList());
    }

    /* -------- tests for getById method -------- */

    /**
     * Tests method for fetching a null address.  Expected to throw a NullPointerException.
     * 
     * @throws UnknownHostException Should not happen.
     */
    @Test
    public void getById_null_throwsNullPointer() throws UnknownHostException {
        assertThrows(NullPointerException.class, () -> {
            service.getById(null);
        });
    }

    /**
     * Tests method for fetching an IpAddress that does not exist.  Expected to return an empty Optional.
     * 
     * @throws UnknownHostException Should not happen.
     */
    @Test
    public void getById_doesNotExist_returnEmptyOptional() throws UnknownHostException {
        // setup test
        Optional<IpAddress> ipAddressOpt = Optional.empty();
        Mockito.when(repository.findById(16_777_216L)).thenReturn(ipAddressOpt);
        
        // execute test
        Optional<IpAddress> result = service.getById("1.0.0.0");
        
        // verify result
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    /**
     * Tests method for fetching an IpAddress that does exist.  Expected to return an Optional populated with the
     * IpAddress.
     * 
     * @throws UnknownHostException Should not happen.
     */
    @Test
    public void getById_exists_returnPopulatedOptional() throws UnknownHostException {
        // setup test
        IpAddress ipAddress = new IpAddress(16_777_216L, false);
        Optional<IpAddress> ipAddressOpt = Optional.of(ipAddress);
        Mockito.when(repository.findById(16_777_216L)).thenReturn(ipAddressOpt);
        
        // execute test
        Optional<IpAddress> result = service.getById("1.0.0.0");
        
        // verify result
        assertNotNull(result);
        assertEquals(ipAddress, result.get());
    }

    /* -------- tests for release method -------- */

    /**
     * Tests method for releasing a null address.  Expected to throw a NullPointerException.
     * 
     * @throws UnknownHostException Should not happen.
     * @throws IpAddressNotFoundException Should not happen.
     */
    @Test
    public void release_null_throwsNullPointer() throws UnknownHostException, IpAddressNotFoundException {
        assertThrows(NullPointerException.class, () -> {
            service.release(null);
        });
    }

    /**
     * Tests method for releasing an IpAddress that does not exist.  Expected to throw an IpAddressNotFoundException.
     * 
     * @throws UnknownHostException Should not happen.
     */
    @Test
    public void release_doesNotExist_throwsIpAddressNotFound() throws UnknownHostException {
        // setup test
        Optional<IpAddress> ipAddressOpt = Optional.empty();
        Mockito.when(repository.findById(16_777_216L)).thenReturn(ipAddressOpt);
        
        // execute test
        assertThrows(IpAddressNotFoundException.class, () -> {
            service.release("1.0.0.0");
        });
    }

    /**
     * Tests method for releasing an IpAddress that exists with acquired set true.  Expected to set the acquired flag 
     * false for it, save it to DB, and return it.
     * 
     * @throws UnknownHostException Should not happen.
     * @throws IpAddressNotFoundException Should not happen.
     */
    @Test
    public void release_existsAcquired_setsAcquireFalseAndSaves_returnsIpAddress() throws UnknownHostException, 
        IpAddressNotFoundException 
    {
        // setup test
        IpAddress ipAddress = new IpAddress(16_777_216L, true);
        Optional<IpAddress> ipAddressOpt = Optional.of(ipAddress);
        Mockito.when(repository.findById(16_777_216L)).thenReturn(ipAddressOpt);
        Mockito.when(repository.save(ipAddress)).thenReturn(ipAddress);
        
        // execute test
        IpAddress result = service.release("1.0.0.0");

        // verify result
        assertNotNull(result);
        assertFalse(result.isAcquired());
    }

    /**
     * Tests method for releasing an IpAddress that exists with acquired set false.  Expected do nothing and return it.
     * 
     * @throws UnknownHostException Should not happen.
     * @throws IpAddressNotFoundException Should not happen.
     */
    @Test
    public void release_existsNotAcquired_returnsIpAddress() throws UnknownHostException, 
        IpAddressNotFoundException 
    {
        // setup test
        IpAddress ipAddress = new IpAddress(16_777_216L, false);
        Optional<IpAddress> ipAddressOpt = Optional.of(ipAddress);
        Mockito.when(repository.findById(16_777_216L)).thenReturn(ipAddressOpt);
        
        // execute test
        IpAddress result = service.release("1.0.0.0");

        // verify result
        assertNotNull(result);
        assertFalse(result.isAcquired());
    }
}
