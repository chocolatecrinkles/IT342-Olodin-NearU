package edu.cit.olodin.feature.listing;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.cit.olodin.feature.listing.Listing;
import edu.cit.olodin.feature.listing.ListingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ListingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ListingRepository listingRepository;

    @Autowired
    private ObjectMapper objectMapper;


    @BeforeEach
    void setup() {
        listingRepository.deleteAll();

        Listing restaurant = new Listing();
        restaurant.setName("McDonalds");
        restaurant.setAddress("Cebu City");
        restaurant.setListingType(Type.SERVICE);
        restaurant.setCategory(Category.RESTAURANT);
        restaurant.setPricingType(PricingType.RANGE);
        restaurant.setMinPrice(100.0);
        restaurant.setMaxPrice(265.0);
        restaurant.setOwnerId(99L);

        Listing dorm = new Listing();
        dorm.setName("Student Dorm");
        dorm.setAddress("Talamban");
        dorm.setListingType(Type.ACCOMMODATION);
        dorm.setCategory(Category.DORM);
        dorm.setPricingType(PricingType.MONTHLY);
        dorm.setPrice(2500.0);
        dorm.setOwnerId(99L);

        listingRepository.save(restaurant);
        listingRepository.save(dorm);
    }

    @Test
    @WithMockUser(roles = "BUSINESS_OWNER")
    void searchListingsByKeyword() throws Exception {

        mockMvc.perform(get("/api/listings")
                        .param("keyword", "McDonalds")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("McDonalds"));
    }

    @Test
    @WithMockUser(roles = "BUSINESS_OWNER")
    void filterListingsByCategory() throws Exception {

        mockMvc.perform(get("/api/listings")
                        .param("category", "RESTAURANT")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].category").value("RESTAURANT"));
    }
}