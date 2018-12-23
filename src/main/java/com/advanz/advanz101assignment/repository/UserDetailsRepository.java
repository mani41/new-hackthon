/**
 * 
 */
package com.advanz.advanz101assignment.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.advanz.advanz101assignment.model.UserDetails;

/**
 * @author Mayank
 *
 */
@Repository
public interface UserDetailsRepository extends CrudRepository<UserDetails, Long> {

	
}