package com.inhouse.marketplace.service;

import com.inhouse.marketplace.entity.Proposal;
import java.util.List;

/**
 * Service contract for Proposal operations.
 */
public interface IProposalService {

    Proposal addProposal(Proposal prop);

    Proposal editProposal(Proposal prop);

    Proposal getProposal(int propId);

    void removeProposal(int propId);

    List<Proposal> getAllProposals();
}
