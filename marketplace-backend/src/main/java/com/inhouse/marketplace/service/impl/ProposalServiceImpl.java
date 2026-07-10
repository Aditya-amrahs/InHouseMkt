package com.inhouse.marketplace.service.impl;

import com.inhouse.marketplace.entity.Proposal;
import com.inhouse.marketplace.entity.Resource;
import com.inhouse.marketplace.exception.ResourceNotFoundException;
import com.inhouse.marketplace.repository.IProposalRepository;
import com.inhouse.marketplace.repository.IResourceRepository;
import com.inhouse.marketplace.service.IProposalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Implementation of IProposalService.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ProposalServiceImpl implements IProposalService {

    private final IProposalRepository proposalRepository;
    private final IResourceRepository resourceRepository;

    @Override
    public Proposal addProposal(Proposal prop) {
        // Validate resource reference exists and replace the placeholder
        // (deserialized from JSON) with the managed entity of the real subtype.
        if (prop.getResource() == null) {
            throw new ResourceNotFoundException("Resource", "null");
        }
        Resource resource = resourceRepository.findById(prop.getResource().getResId())
                .orElseThrow(() -> new ResourceNotFoundException("Resource", prop.getResource().getResId()));
        prop.setResource(resource);
        prop.setProposalDate(LocalDate.now());
        prop.setAccepted(false);
        return proposalRepository.save(prop);
    }

    @Override
    public Proposal editProposal(Proposal prop) {
        if (!proposalRepository.existsById(prop.getPropId())) {
            throw new ResourceNotFoundException("Proposal", prop.getPropId());
        }
        return proposalRepository.save(prop);
    }

    @Override
    @Transactional(readOnly = true)
    public Proposal getProposal(int propId) {
        return proposalRepository.findById(propId)
                .orElseThrow(() -> new ResourceNotFoundException("Proposal", propId));
    }

    @Override
    public void removeProposal(int propId) {
        if (!proposalRepository.existsById(propId)) {
            throw new ResourceNotFoundException("Proposal", propId);
        }
        proposalRepository.deleteById(propId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Proposal> getAllProposals() {
        return proposalRepository.findAll();
    }
}
