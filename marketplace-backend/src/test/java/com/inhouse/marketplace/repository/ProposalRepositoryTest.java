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
 * Integration tests for IProposalRepository using H2 in-memory database.
 */
@DataJpaTest
@DisplayName("ProposalRepository Integration Tests")
class ProposalRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private IProposalRepository proposalRepository;

    private Employee proposer;
    private Requirement requirement;

    @BeforeEach
    void setUp() {
        User owner = User.builder().userId("owner@company.com").password("pass").build();
        em.persist(owner);
        Employee reqOwner = Employee.builder()
                .empName("Req Owner").deptName("HR").location("Delhi").user(owner).build();
        em.persist(reqOwner);

        User propUser = User.builder().userId("proposer@company.com").password("pass").build();
        em.persist(propUser);
        proposer = Employee.builder()
                .empName("Proposer").deptName("IT").location("Hyderabad").user(propUser).build();
        em.persist(proposer);

        requirement = Requirement.builder()
                .title("Need a room").category("Accommodation").type("RENT")
                .date(LocalDate.now()).emp(reqOwner).build();
        em.persist(requirement);
        em.flush();
    }

    @Test
    @DisplayName("testAddProposal_ValidData_LinksToResource")
    void testAddProposal_ValidData_LinksToResource() {
        Proposal proposal = Proposal.builder()
                .proposal("I have a 2BHK flat nearby")
                .amount(9000.0)
                .proposalDate(LocalDate.now())
                .isAccepted(false)
                .emp(proposer)
                .resource(requirement)
                .build();

        Proposal saved = proposalRepository.save(proposal);

        assertThat(saved.getPropId()).isPositive();
        assertThat(saved.getResource().getResId()).isEqualTo(requirement.getResId());
        assertThat(saved.isAccepted()).isFalse();
    }

    @Test
    @DisplayName("testProposalAcceptance_MarkAsAccepted")
    void testProposalAcceptance_MarkAsAccepted() {
        Proposal proposal = Proposal.builder()
                .proposal("I can help").amount(5000.0)
                .proposalDate(LocalDate.now()).isAccepted(false)
                .emp(proposer).resource(requirement).build();
        Proposal saved = proposalRepository.save(proposal);

        saved.setAccepted(true);
        saved.setAcceptedOn(LocalDate.now());
        Proposal updated = proposalRepository.save(saved);

        assertThat(updated.isAccepted()).isTrue();
        assertThat(updated.getAcceptedOn()).isNotNull();
    }

    @Test
    @DisplayName("testDeleteProposal_ExistingId_RemovesProposal")
    void testDeleteProposal_ExistingId_RemovesProposal() {
        Proposal proposal = Proposal.builder()
                .proposal("To be deleted").amount(1000.0)
                .proposalDate(LocalDate.now()).isAccepted(false)
                .emp(proposer).resource(requirement).build();
        Proposal saved = proposalRepository.save(proposal);
        int id = saved.getPropId();

        proposalRepository.deleteById(id);

        Optional<Proposal> result = proposalRepository.findById(id);
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("testGetAllProposals_ReturnsFullList")
    void testGetAllProposals_ReturnsFullList() {
        Proposal p1 = Proposal.builder()
                .proposal("P1").amount(2000.0).proposalDate(LocalDate.now())
                .isAccepted(false).emp(proposer).resource(requirement).build();
        Proposal p2 = Proposal.builder()
                .proposal("P2").amount(3000.0).proposalDate(LocalDate.now())
                .isAccepted(false).emp(proposer).resource(requirement).build();

        proposalRepository.save(p1);
        proposalRepository.save(p2);

        List<Proposal> all = proposalRepository.findAll();

        assertThat(all).hasSizeGreaterThanOrEqualTo(2);
    }
}
