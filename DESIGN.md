# Design Notes

## Clarifications

The requirements used an example of using the application to an IPv4 CIDR block.  Because of that I am going to
make the simplifying assumption that we don't need to support IPv6 syntax for this coding challenge.

The requirements do not specify, but I'm assuming that the operation to retrieve all should return addresses in
ascending order, and that the operation to add a new CIDR block should reject the attempt if the new block overlaps 
any existing block.

I am also assuming that messages in the application can be in English, and don't need to be internationalizable.

## Discussion

My proposed approach for this application is to persist IpAddress entities to the DB, with operations to create, 
retrieve all, and update (i.e. acquire/release) those entities.  Though the requirements don't call for it, I propose 
to add a delete all operation as well, to facilitate testing.  

The IpAddress entity will encapsulate an address attribute and a flag indicating whether it has been acquired.  The 
address attribute will be stored as a long, to facilitate searching and sorting.  The presentation of an IpAddress 
entity REST callers however should use a dotted decimal version of the address instead, for readability.  Because of 
this, we will return IpAddressDTO instances to REST callers, instead of IpAddress instances.

CIDR blocks can be quite large, spanning up to 2^32 addresses.  Though not mentioned in the requirements, we should try 
to avoid holding large numbers of addresses in memory simultaneously within the application.  This means both during 
the create operation that adds a new CIDR block, and during the retrieve operation.  To minimize memory usage in the 
create operation, we will perform the instantiation and persistence of IpAddresses in batches, and to minimize memory 
usage in the retrieve operation, we will offer a paginated retrieval operation.  The retrieve all operation will also
be offered, for convenience when the caller is confident that the number of addresses returned is not too large.  

Note that a future version of the application could be made more sophisticated by streaming results in the retrieve all
operation, rather than holding them all in memory simultaneously.  It could also be made more sophisticated by storing 
IpAddressRange instances in the DB for each CIDR block that is added, which would define the boundaries of the block, 
and only storing IpAddress instances in the DB once they are acquired.  I am not pursuing these enhancements now 
however, for brevity during this coding challenge.

## Technology

This solution uses the following technologies:

- SpringBoot with an embedded Tomcat server for handling REST requests.  
- JPA/Hibernate with an embedded H2 database for persistence.  
- Apache Commons Net for subnet related utilities.
- OpenAPI/Swagger for API documentation.
- Gradle for construction.
- Checkstyle/PMD for static code analysis.
- JUnit/Mockito for testing.

