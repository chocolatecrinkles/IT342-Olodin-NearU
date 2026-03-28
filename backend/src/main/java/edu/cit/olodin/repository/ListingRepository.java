package edu.cit.olodin.repository;

import edu.cit.olodin.entity.Listing;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ListingRepository  extends JpaRepository <Listing, Long>{
    List<Listing> findByOwnerId(Long ownerId);
}
