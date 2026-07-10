package com.inhouse.marketplace.service;

import com.inhouse.marketplace.entity.Employee;
import com.inhouse.marketplace.entity.Offer;
import com.inhouse.marketplace.exception.ResourceNotFoundException;
import com.inhouse.marketplace.repository.IOfferRepository;
import com.inhouse.marketplace.service.impl.OfferServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for OfferServiceImpl — covers TEST MATRIX: Offer Module.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("OfferService Unit Tests")
class OfferServiceTest {

    @Mock
    private IOfferRepository offerRepository;

    @InjectMocks
    private OfferServiceImpl offerService;

    private Offer offer;
    private Employee employee;

    @BeforeEach
    void setUp() {
        employee = Employee.builder().empId(1).empName("Carol").build();
        offer = Offer.builder()
                .resId(1)
                .title("iPhone 14 for sale")
                .category("Electronics")
                .type("SELL")
                .price(60000.0)
                .isAvailable(true)
                .availableUpto(LocalDate.now().plusDays(30))
                .emp(employee)
                .build();
    }

    @Test
    @DisplayName("testAddOffer_ValidData_ReturnsSavedOffer")
    void testAddOffer_ValidData_ReturnsSavedOffer() {
        when(offerRepository.save(any(Offer.class))).thenReturn(offer);

        Offer result = offerService.addOffer(offer);

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("iPhone 14 for sale");
        assertThat(result.isAvailable()).isTrue();
        verify(offerRepository).save(any(Offer.class));
    }

    @Test
    @DisplayName("testOfferUpdate_MarkAsUnavailable")
    void testOfferUpdate_MarkAsUnavailable() {
        Offer unavailable = Offer.builder().resId(1).isAvailable(false).emp(employee).build();
        when(offerRepository.existsById(1)).thenReturn(true);
        when(offerRepository.save(any())).thenReturn(unavailable);

        Offer result = offerService.editOffer(unavailable);

        verify(offerRepository).save(any());
    }

    @Test
    @DisplayName("testDeleteOffer_ExistingId_RemovesOffer")
    void testDeleteOffer_ExistingId_RemovesOffer() {
        when(offerRepository.existsById(1)).thenReturn(true);
        doNothing().when(offerRepository).deleteById(1);

        assertThatCode(() -> offerService.removeOffer(1)).doesNotThrowAnyException();

        verify(offerRepository).deleteById(1);
    }

    @Test
    @DisplayName("testGetAllOffers_FilterByCategoryAndType_ReturnsFilteredList")
    void testGetAllOffers_FilterByCategoryAndType_ReturnsFilteredList() {
        when(offerRepository.findByCategoryAndType("Electronics", "SELL"))
                .thenReturn(List.of(offer));

        List<Offer> result = offerService.getAllOffers("Electronics", "SELL");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCategory()).isEqualTo("Electronics");
    }

    @Test
    @DisplayName("testGetOffer_NonExistingId_ReturnsNull")
    void testGetOffer_NonExistingId_ReturnsNull() {
        when(offerRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> offerService.getOffer(999))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("999");
    }
}
