package com.inhouse.marketplace.service.impl;

import com.inhouse.marketplace.entity.Offer;
import com.inhouse.marketplace.exception.ResourceNotFoundException;
import com.inhouse.marketplace.repository.IOfferRepository;
import com.inhouse.marketplace.service.IOfferService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Implementation of IOfferService.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class OfferServiceImpl implements IOfferService {

    private final IOfferRepository offerRepository;

    @Override
    public Offer addOffer(Offer offer) {
        offer.setDate(LocalDate.now());
        offer.setAvailable(true);
        return offerRepository.save(offer);
    }

    @Override
    public Offer editOffer(Offer offer) {
        if (!offerRepository.existsById(offer.getResId())) {
            throw new ResourceNotFoundException("Offer", offer.getResId());
        }
        return offerRepository.save(offer);
    }

    @Override
    @Transactional(readOnly = true)
    public Offer getOffer(int offerId) {
        return offerRepository.findById(offerId)
                .orElseThrow(() -> new ResourceNotFoundException("Offer", offerId));
    }

    @Override
    public void removeOffer(int offerId) {
        if (!offerRepository.existsById(offerId)) {
            throw new ResourceNotFoundException("Offer", offerId);
        }
        offerRepository.deleteById(offerId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Offer> getAllOffers() {
        return offerRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Offer> getAllOffers(String category, String type) {
        if (category != null && type != null) {
            return offerRepository.findByCategoryAndType(category, type);
        }
        if (category != null) {
            return offerRepository.findByCategory(category);
        }
        if (type != null) {
            return offerRepository.findByType(type);
        }
        return offerRepository.findAll();
    }
}
