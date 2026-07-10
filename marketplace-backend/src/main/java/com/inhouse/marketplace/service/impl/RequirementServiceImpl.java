package com.inhouse.marketplace.service.impl;

import com.inhouse.marketplace.entity.Requirement;
import com.inhouse.marketplace.exception.ResourceNotFoundException;
import com.inhouse.marketplace.repository.IRequirementRepository;
import com.inhouse.marketplace.service.IRequirementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Implementation of IRequirementService.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class RequirementServiceImpl implements IRequirementService {

    private final IRequirementRepository requirementRepository;

    @Override
    public Requirement addRequirement(Requirement req) {
        req.setDate(LocalDate.now());
        return requirementRepository.save(req);
    }

    @Override
    public Requirement editRequirement(Requirement req) {
        if (!requirementRepository.existsById(req.getResId())) {
            throw new ResourceNotFoundException("Requirement", req.getResId());
        }
        return requirementRepository.save(req);
    }

    @Override
    @Transactional(readOnly = true)
    public Requirement getRequirement(int reqId) {
        return requirementRepository.findById(reqId)
                .orElseThrow(() -> new ResourceNotFoundException("Requirement", reqId));
    }

    @Override
    public void removeRequirement(int reqId) {
        if (!requirementRepository.existsById(reqId)) {
            throw new ResourceNotFoundException("Requirement", reqId);
        }
        requirementRepository.deleteById(reqId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Requirement> getAllRequirements() {
        return requirementRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Requirement> getAllRequirements(String category, String type) {
        if (category != null && type != null) {
            return requirementRepository.findByCategoryAndType(category, type);
        }
        if (category != null) {
            return requirementRepository.findByCategory(category);
        }
        if (type != null) {
            return requirementRepository.findByType(type);
        }
        return requirementRepository.findAll();
    }
}
