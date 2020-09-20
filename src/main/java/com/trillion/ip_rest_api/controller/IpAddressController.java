package com.trillion.ip_rest_api.controller;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.trillion.ip_rest_api.dto.IpAddressDTO;
import com.trillion.ip_rest_api.exception.IpAddressNotFoundException;
import com.trillion.ip_rest_api.exception.IpAddressOverlapException;
import com.trillion.ip_rest_api.model.IpAddress;
import com.trillion.ip_rest_api.service.IpAddressService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

/**
 * Provides public REST wrapper around address related operations. 
 */
@RestController     
@RequestMapping("/api/address")
public class IpAddressController {
    /**
     * Service to use for address related operations.
     */
    @Autowired
    private IpAddressService service;
    
    /**
     * Updates a specified IpAddress instance to set its acquired flag to true (if it is not already).
     * <p>
     * Note that the PatchMapping specification below includes a trailing ".+" specification so that Spring does not
     * strip off the trailing portion of the dotted decimal address.
     * 
     * @param address Dotted decimal address to update.
     * @return IpAddressDTO representation of IpAddress that was updated.
     * @throws IpAddressNotFoundException Thrown if address does not exist in DB.
     * @throws UnknownHostException Thrown if unable to translate input address.
     */
    @Operation(summary = "Mark the specified dotted decimal address as acquired.")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Acquired successfully.",
            content = { 
                @Content(
                    mediaType = "application/json", 
                    schema = @Schema(implementation = IpAddress.class)
                ) 
            }
        ),
        @ApiResponse(responseCode = "400", description = "Invalid address.", content = @Content),
        @ApiResponse(responseCode = "404", description = "Address not found.", content = @Content)
    })
    @PatchMapping("/acquire/{address:.+}")
    public IpAddressDTO acquire(@PathVariable String address) throws IpAddressNotFoundException, UnknownHostException {
        IpAddress ipAddress = service.acquire(address);
        return new IpAddressDTO(ipAddress);
    }

    /**
     * Adds IpAddress instances for all addresses within (inclusive) the specified CIDR block.  
     * 
     * @param networkAddress Dotted decimal address of CIDR block being added (e.g. "1.0.0.0").
     * @param cidrMask CIDR mask of the block being added (e.g. 24).
     * @return Count of the number of IpAddress instances that were added.
     * @throws UnknownHostException Thrown if unable to translate input address successfully.
     * @throws IpAddressOverlapException THrown if the CIDR block overlap existing IpAddressRanges in DB.
     */
    @Operation(summary = 
        "Adds new IpAddress instances within (inclusive) the specified CIDR block (e.g. '1.0.0.0/24')."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Added successfully.",
            content = { 
                @Content(
                    mediaType = "text/plain", 
                    schema = @Schema(type = "integer", format = "int64")
                ) 
            }
        ),
        @ApiResponse(responseCode = "400", description = "Invalid CIDR block.", content = @Content),
        @ApiResponse(responseCode = "409", description = "Overlapping CIDR block.", content = @Content)
    })
    @PostMapping("/{networkAddress}/{cidrMask}")
    public long addBlock(@PathVariable String networkAddress, @PathVariable int cidrMask) 
        throws UnknownHostException, IpAddressOverlapException 
    {
        return service.addBlock(networkAddress, cidrMask);
    }    
    
    /**
     * Internal utility for converting a list of IpAddress instances to a list of IpAddressDTO instances.  Note that
     * we do not use stream mapping for this because the IpAddressDTO constructor can throw a checked exception, which  
     * is not something that stream mapping can tolerate.
     * 
     * @param ipAddresses List of IpAddress instances in question.
     * @return List of corresponding IpAddressDTO instances created.
     * @throws UnknownHostException Thrown if we cannot convert a long address to dotted decimals (should not happen).
     */
    private List<IpAddressDTO> convertIpAddressesToDTOs(List<IpAddress> ipAddresses) throws UnknownHostException {
        List<IpAddressDTO> ipAddressDTOs = new ArrayList<>(ipAddresses.size());
        for (IpAddress ipAddress : ipAddresses) {
            IpAddressDTO ipAddressDTO = new IpAddressDTO(ipAddress);
            ipAddressDTOs.add(ipAddressDTO);
        }
        return ipAddressDTOs;
    }
    
    /**
     * Deletes all IpAddress instances.
     */
    @Operation(summary = "Delete all IpAddress instances.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Deleted successfully (if any).", content = @Content)
    })
    @DeleteMapping("")
    public void deleteAll() {
        service.deleteAll();
    } 
    
    /**
     * Fetches all IpAddress instances present, in ascending order.
     * 
     * @return List of addresses present.
     * @throws UnknownHostException Thrown if we cannot convert a long address to dotted decimals (should not happen).
     */
    @Operation(summary = "Fetch all IpAddresses.")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Fetched successfully.",
            content = { 
                @Content(
                    mediaType = "application/json", 
                    array = @ArraySchema(schema = @Schema(implementation = IpAddressDTO.class))
                ) 
            }
        )
    })
    @GetMapping("")
    public List<IpAddressDTO> getAll() throws UnknownHostException {
        List<IpAddress> ipAddresses = service.getAll();
        return convertIpAddressesToDTOs(ipAddresses);
    }
    
    /**
     * Fetches one page of IpAddress instances, in ascending order.  
     * 
     * @param pageNum Page number to fetch.
     * @param pageSize Number of instances in a page.
     * @param resp HttpServletResponse we will send back.
     * @return List of addresses present.
     * @throws UnknownHostException Thrown if we cannot convert a long address to dotted decimals (should not happen).
     */
    @Operation(summary = 
        "Fetch one page of IpAddress instances, using the specified page number (starting at 0) and page size."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Fetched successfully.",
            content = { 
                @Content(
                    mediaType = "application/json", 
                    array = @ArraySchema(schema = @Schema(implementation = IpAddressDTO.class))
                ) 
            }
        )
    })
    @GetMapping("/{pageNum}/{pageSize}")
    public List<IpAddressDTO> getPage(@PathVariable int pageNum, @PathVariable int pageSize, HttpServletResponse resp) 
        throws UnknownHostException
    {
        List<IpAddress> ipAddresses = service.getPage(pageNum, pageSize);
        return convertIpAddressesToDTOs(ipAddresses);
    }
    
    /**
     * Updates a specified IpAddress instance to set its acquired flag to false (if it is not already).
     * <p>
     * Note that the PatchMapping specification below includes a trailing ".+" specification so that Spring does not
     * strip off the trailing portion of the dotted decimal address.
     * 
     * @param address Dotted decimal address to update.
     * @return IpAddressDTO representation of IpAddress that was updated.
     * @throws IpAddressNotFoundException Thrown if address does not exist in DB.
     * @throws UnknownHostException Thrown if unable to translate input address.
     */
    @Operation(summary = "Mark the specified dotted decimal address as released.")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Released successfully.",
            content = { 
                @Content(
                    mediaType = "application/json", 
                    schema = @Schema(implementation = IpAddress.class)
                ) 
            }
        ),
        @ApiResponse(responseCode = "400", description = "Invalid address.", content = @Content),
        @ApiResponse(responseCode = "404", description = "Address not found.", content = @Content)
    })
    @PatchMapping("/release/{address:.+}")
    public IpAddressDTO release(@PathVariable String address) throws IpAddressNotFoundException, UnknownHostException {
        IpAddress ipAddress = service.release(address);
        return new IpAddressDTO(ipAddress);
    }
}
