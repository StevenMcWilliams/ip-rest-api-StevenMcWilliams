package com.trillion.ip_rest_api.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.trillion.ip_rest_api.model.IpAddress;
 
/**
 * Specifies DB CRUD operations for IpAddress instances.  
 */
@Repository
public interface IpAddressRepository extends PagingAndSortingRepository<IpAddress, Long> {
    /**
     * Query to test whether any IpAddress instances exist in the specified range (inclusive).
     * 
     * @param startAddress Start address of range in question.
     * @param endAddress End address of range in question.
     * @return Returns true if any exist in the range.
     */
    @Query(
        "select case when count(ia) > 0 then true else false end from IpAddress ia " +
        "where (address >= :start_address) and (address <= :end_address)"
    )
    boolean existsWithinRange(@Param("start_address") long startAddress, @Param("end_address") long endAddress);
}
