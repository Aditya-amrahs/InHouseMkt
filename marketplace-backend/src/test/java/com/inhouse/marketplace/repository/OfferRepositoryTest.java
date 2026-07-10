package com.inhouse.marketplace.repository;

import com.inhouse.marketplace.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

/**
 * Integration tests for IOfferRepository using H2 in-memory database.
 */
@DataJpaTest
@DisplayName("OfferRepository Integration Tests")
class OfferRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private IOfferRepository offerRepository;

    private Employee employee;

    @BeforeEach
    void setUp() {
        User user = User.builder().userId("offer_test@company.com").password("pass").build();
        em.persist(user);

        employee = Employee.builder()
                .empName("Offer Tester")
                .deptName("Sales")
                .location("Mumbai")
                .user(user)
                .build();
        em.persist(employee);
        em.flush();
    }

    @Test
    @DisplayName("testAddOffer_ValidData_ReturnsSavedOffer")
    void testAddOffer_ValidData_ReturnsSavedOffer() {
        Offer offer = Offer.builder()
                .title("Selling Macbook")
                .category("Electronics")
                .type("SELL")
                .price(80000.0)
                .isAvailable(true)
                .availableUpto(LocalDate.now().plusDays(14))
                .date(LocalDate.now())
                .emp(employee)
                .build();

        Offer saved = offerRepository.save(offer);

        assertThat(saved.getResId()).isPositive();
        assertThat(saved.isAvailable()).isTrue();
    }

    @Test
    @DisplayName("testGetAllOffers_FilterByCategoryAndType_ReturnsFilteredList")
    void testGetAllOffers_FilterByCategoryAndType_ReturnsFilteredList() {
        Offer o1 = Offer.builder()
                .title("Laptop").category("Electronics").type("SELL")
                .isAvailable(true).date(LocalDate.now()).emp(employee).build();
        Offer o2 = Offer.builder()
                .title("Bike").category("Vehicle").type("RENT")
                .isAvailable(true).date(LocalDate.now()).emp(employee).build();

        offerRepository.save(o1);
        offerRepository.save(o2);

        List<Offer> result = offerRepository.findByCategoryAndType("Electronics", "SELL");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Laptop");
    }

    @Test
    @DisplayName("testOfferUpdate_MarkAsUnavailable")
    void testOfferUpdate_MarkAsUnavailable() {
        Offer offer = Offer.builder()
                .title("Camera").category("Electronics").type("SELL")
                .isAvailable(true).date(LocalDate.now()).emp(employee).build();
        Offer saved = offerRepository.save(offer);

        saved.setAvailable(false);
        Offer updated = offerRepository.save(saved);

        assertThat(updated.isAvailable()).isFalse();
    }

    @Test
    @DisplayName("testDeleteOffer_ExistingId_RemovesOffer")
    void testDeleteOffer_ExistingId_RemovesOffer() {
        Offer offer = Offer.builder()
                .title("Watch").category("Accessories").type("SELL")
                .isAvailable(true).date(LocalDate.now()).emp(employee).build();
        Offer saved = offerRepository.save(offer);
        int id = saved.getResId();

        offerRepository.deleteById(id);

        assertThat(offerRepository.findById(id)).isEmpty();
    }

    @Test
    @DisplayName("testGetOffer_NonExistingId_ReturnsEmpty")
    void testGetOffer_NonExistingId_ReturnsEmpty() {
        Optional<Offer> result = offerRepository.findById(99999);

        assertThat(result).isEmpty();
    }
}
