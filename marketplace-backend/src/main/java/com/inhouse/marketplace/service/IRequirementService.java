package com.inhouse.marketplace.service;

import com.inhouse.marketplace.entity.Requirement;
import java.util.List;

/**
 * Service contract for Requirement CRUD and filtering.
 */
public interface IRequirementService {

    Requirement addRequirement(Requirement req);

    Requirement editRequirement(Requirement req);

    Requirement getRequirement(int reqId);

    void removeRequirement(int reqId);

    List<Requirement> getAllRequirements();

    List<Requirement> getAllRequirements(String category, String type);
}
