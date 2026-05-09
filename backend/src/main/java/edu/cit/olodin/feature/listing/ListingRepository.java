package edu.cit.olodin.feature.listing;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ListingRepository  extends JpaRepository <Listing, Long>{
    List<Listing> findByOwnerId(Long ownerId);
}
