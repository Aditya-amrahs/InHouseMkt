package com.inhouse.marketplace.service;

import com.inhouse.marketplace.entity.Offer;
import java.util.List;

/**
 * Service contract for Offer CRUD and filtering.
 */
public interface IOfferService {

    Offer addOffer(Offer offer);

    Offer editOffer(Offer offer);

    Offer getOffer(int offerId);

    void removeOffer(int offerId);

    List<Offer> getAllOffers();

    List<Offer> getAllOffers(String category, String type);
}
