package com.trillion.ip_rest_api.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trillion.ip_rest_api.dto.IpAddressDTO;
import com.trillion.ip_rest_api.exception.ExceptionHandlerAdvice;
import com.trillion.ip_rest_api.exception.IpAddressNotFoundException;
import com.trillion.ip_rest_api.exception.IpAddressOverlapException;
import com.trillion.ip_rest_api.model.IpAddress;
import com.trillion.ip_rest_api.service.IpAddressService;

/**
 * Unit tests for IpAddressController.
 * <p>
 * Note that I am not bothering to test the deleteAll, due to its simplicity, nor am I testing getPage due to its
 * similarity to getAll.
 */
@ExtendWith(SpringExtension.class)
@Tag("controller")
public class IpAddressControllerTest {
    /**
     * Controller instance used in our tests.  Will be constructed to use our mock service attribute.
     */
    @InjectMocks
    private IpAddressController controller;

    /**
     * Provides ability to send REST request to our controller and gather response, without running app server.
     */
    private MockMvc mockMvc;
    
    /**
     * Mock IpAddressService instance for our controller to use.
     */
    @Mock
    private IpAddressService service;

    /**
     * Instantiates mockMvc attribute to use our controller attribute for receiving REST requests it sends.
     */
    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).setControllerAdvice(new ExceptionHandlerAdvice()).build();
    }

    /* -------- tests for acquire method -------- */
    
    /**
     * Verifies that attempt to acquire an invalid address returns a 400 status.
     *
     * @throws Exception Thrown on unexpected REST communication errors from mockMvc.
     */
    @Test
    public void acquire_invalid_returns400() throws Exception {
        // setup for test
        Mockito.when(service.acquire("1.0.0")).thenThrow(UnknownHostException.class);

        // execute test method and verify response status
        mockMvc.perform(patch("/api/address/acquire/1.0.0")).
            andExpect(status().isBadRequest()).
            andReturn();
    }

    /**
     * Verifies that attempt to acquire a non-existent IpAddress instance returns a 404 status.
     *
     * @throws Exception Thrown on unexpected REST communication errors from mockMvc.
     */
    @Test
    public void acquire_exists_returns404() throws Exception {
        // setup for test
        Mockito.when(service.acquire("1.0.0.0")).thenThrow(IpAddressNotFoundException.class);

        // execute test method and verify response status
        mockMvc.perform(patch("/api/address/acquire/1.0.0.0")).
            andExpect(status().isNotFound()).
            andReturn();
    }

    /**
     * Verifies that attempt to acquire an existing IpAddress instance returns a 200 status and a corresponding 
     * IpAddressDTO instance.
     *
     * @throws Exception Thrown on unexpected REST communication errors from mockMvc.
     */
    @Test
    public void acquire_exists_returns200AndDTO() throws Exception {
        // setup for test
        IpAddress ipAddress = new IpAddress(16_777_216L, true);
        Mockito.when(service.acquire("1.0.0.0")).thenReturn(ipAddress);
        IpAddressDTO expectedIpAddressDTO = new IpAddressDTO("1.0.0.0", true);

        // execute test method and verify response status
        MvcResult result = mockMvc.perform(patch("/api/address/acquire/1.0.0.0")).
            andExpect(status().isOk()).
            andReturn();

        // verify response body
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
        MockHttpServletResponse response = result.getResponse();
        String resultIpAddressDTOJson = response.getContentAsString();
        IpAddressDTO resultIpAddressDTO = objectMapper.readValue(resultIpAddressDTOJson, IpAddressDTO.class);
        assertEquals(expectedIpAddressDTO, resultIpAddressDTO);
    }
    
    /* -------- tests for addBlock method -------- */
    
    /**
     * Verifies that attempt to add an invalid CIDR block returns a 400 status.
     *
     * @throws Exception Thrown on unexpected REST communication errors from mockMvc.
     */
    @Test
    public void addBlock_invalid_returns400() throws Exception {
        // setup for test
        Mockito.when(service.addBlock("1.0.0", 30)).thenThrow(UnknownHostException.class);

        // execute test method and verify response status
        mockMvc.perform(post("/api/address/1.0.0/30")).
            andExpect(status().isBadRequest()).
            andReturn();
    }
    
    /**
     * Verifies that attempt to add an overlapping CIDR block returns a 409 status.
     *
     * @throws Exception Thrown on unexpected REST communication errors from mockMvc.
     */
    @Test
    public void addBlock_overlap_returns409() throws Exception {
        // setup for test
        Mockito.when(service.addBlock("1.0.0.0", 30)).thenThrow(IpAddressOverlapException.class);

        // execute test method and verify response status
        mockMvc.perform(post("/api/address/1.0.0.0/30")).
            andExpect(status().isConflict()).
            andReturn();
    }
    
    /**
     * Verifies that attempt to add a non-overlapping CIDR block of size 4 returns a 200 status and count of 4.
     *
     * @throws Exception Thrown on unexpected REST communication errors from mockMvc.
     */
    @Test
    public void addBlockOf4_noOverlap_returns200AndCountOf4() throws Exception {
        // setup for test
        Mockito.when(service.addBlock("1.0.0.0", 30)).thenReturn(4L);

        // execute test method and verify response status
        MvcResult result = mockMvc.perform(post("/api/address/1.0.0.0/30")).
            andExpect(status().isOk()).
            andReturn();

        // verify response body
        MockHttpServletResponse response = result.getResponse();
        String count = response.getContentAsString();
        assertEquals("4", count);
    }
    
    /* -------- tests for getAll method -------- */
    
    /**
     * Verifies that attempt to fetch all IpAddresses from a DB containing 4 returns a 200 status and an array of
     * 4 corresponding IpAddressDTOs.
     *
     * @throws Exception Thrown on unexpected REST communication errors from mockMvc.
     */
    @Test
    public void getAll_returns200AndDTOs() throws Exception {
        // setup for test
        List<IpAddress> ipAddresses = new ArrayList<>();
        ipAddresses.add(new IpAddress(16_777_216L, false));
        ipAddresses.add(new IpAddress(16_777_217L, false));
        ipAddresses.add(new IpAddress(16_777_218L, false));
        ipAddresses.add(new IpAddress(16_777_219L, false));
        Mockito.when(service.getAll()).thenReturn(ipAddresses);

        // execute test method and verify response status
        MvcResult result = mockMvc.perform(get("/api/address")).
            andExpect(status().isOk()).
            andReturn();

        // verify response body
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
        List<IpAddressDTO> expectedIpAddressDTOs = new ArrayList<>();
        expectedIpAddressDTOs.add(new IpAddressDTO("1.0.0.0", false));
        expectedIpAddressDTOs.add(new IpAddressDTO("1.0.0.1", false));
        expectedIpAddressDTOs.add(new IpAddressDTO("1.0.0.2", false));
        expectedIpAddressDTOs.add(new IpAddressDTO("1.0.0.3", false));
        MockHttpServletResponse response = result.getResponse();
        String resultIpAddressDTOsJson = response.getContentAsString();
        List<IpAddressDTO> resultIpAddressDTOs = objectMapper.readValue(resultIpAddressDTOsJson, 
            new TypeReference<List<IpAddressDTO>>() { });
        assertEquals(expectedIpAddressDTOs, resultIpAddressDTOs);
    }
    
    /* -------- tests for release method -------- */
    
    /**
     * Verifies that attempt to release a non-existent IpAddress instance returns a 404 status.
     *
     * @throws Exception Thrown on unexpected REST communication errors from mockMvc.
     */
    @Test
    public void release_exists_returns404() throws Exception {
        // setup for test
        Mockito.when(service.release("1.0.0.0")).thenThrow(IpAddressNotFoundException.class);

        // execute test method and verify response status
        mockMvc.perform(patch("/api/address/release/1.0.0.0")).
            andExpect(status().isNotFound()).
            andReturn();
    }
    
    /**
     * Verifies that attempt to release an invalid address returns a 400 status.
     *
     * @throws Exception Thrown on unexpected REST communication errors from mockMvc.
     */
    @Test
    public void release_invalid_returns400() throws Exception {
        // setup for test
        Mockito.when(service.release("1.0.0")).thenThrow(UnknownHostException.class);

        // execute test method and verify response status
        mockMvc.perform(patch("/api/address/release/1.0.0")).
            andExpect(status().isBadRequest()).
            andReturn();
    }

    /**
     * Verifies that attempt to release an existing IpAddress instance returns a 200 status and a corresponding 
     * IpAddressDTO instance.
     *
     * @throws Exception Thrown on unexpected REST communication errors from mockMvc.
     */
    @Test
    public void release_exists_returns200AndDTO() throws Exception {
        // setup for test
        IpAddress ipAddress = new IpAddress(16_777_216L, false);
        Mockito.when(service.release("1.0.0.0")).thenReturn(ipAddress);
        IpAddressDTO expectedIpAddressDTO = new IpAddressDTO("1.0.0.0", false);
        
        // execute test method and verify response status
        MvcResult result = mockMvc.perform(patch("/api/address/release/1.0.0.0")).
            andExpect(status().isOk()).
            andReturn();

        // verify response body
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
        MockHttpServletResponse response = result.getResponse();
        String resultIpAddressDTOJson = response.getContentAsString();
        IpAddressDTO resultIpAddressDTO = objectMapper.readValue(resultIpAddressDTOJson, IpAddressDTO.class);
        assertEquals(expectedIpAddressDTO, resultIpAddressDTO);
    }
}
