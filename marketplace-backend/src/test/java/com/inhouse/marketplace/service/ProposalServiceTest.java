package com.inhouse.marketplace.service;

import com.inhouse.marketplace.entity.*;
import com.inhouse.marketplace.exception.ResourceNotFoundException;
import com.inhouse.marketplace.repository.IProposalRepository;
import com.inhouse.marketplace.repository.IResourceRepository;
import com.inhouse.marketplace.service.impl.ProposalServiceImpl;
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
 * Unit tests for ProposalServiceImpl — covers TEST MATRIX: Proposal Module.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ProposalService Unit Tests")
class ProposalServiceTest {

    @Mock private IProposalRepository proposalRepository;
    @Mock private IResourceRepository resourceRepository;

    @InjectMocks
    private ProposalServiceImpl proposalService;

    private Employee employee;
    private Requirement requirement;
    private Proposal proposal;

    @BeforeEach
    void setUp() {
        employee = Employee.builder().empId(1).empName("Dave").build();
        requirement = Requirement.builder().resId(1).title("Need PG").emp(employee).build();
        proposal = Proposal.builder()
                .propId(1)
                .proposal("I can offer a 2BHK near office")
                .amount(8000.0)
                .proposalDate(LocalDate.now())
                .isAccepted(false)
                .emp(employee)
                .resource(requirement)
                .build();
    }

    @Test
    @DisplayName("testAddProposal_ValidData_LinksToResource")
    void testAddProposal_ValidData_LinksToResource() {
        when(resourceRepository.findById(1)).thenReturn(Optional.of(requirement));
        when(proposalRepository.save(any(Proposal.class))).thenReturn(proposal);

        Proposal result = proposalService.addProposal(proposal);

        assertThat(result).isNotNull();
        assertThat(result.getResource().getResId()).isEqualTo(1);
        assertThat(result.isAccepted()).isFalse();
        verify(proposalRepository).save(any(Proposal.class));
    }

    @Test
    @DisplayName("testProposalAcceptance_MarkAsAccepted")
    void testProposalAcceptance_MarkAsAccepted() {
        Proposal accepted = Proposal.builder()
                .propId(1).isAccepted(true).acceptedOn(LocalDate.now())
                .emp(employee).resource(requirement).build();

        when(proposalRepository.existsById(1)).thenReturn(true);
        when(proposalRepository.save(any())).thenReturn(accepted);

        Proposal result = proposalService.editProposal(proposal);

        verify(proposalRepository).save(any());
    }

    @Test
    @DisplayName("testDeleteProposal_ExistingId_RemovesProposal")
    void testDeleteProposal_ExistingId_RemovesProposal() {
        when(proposalRepository.existsById(1)).thenReturn(true);
        doNothing().when(proposalRepository).deleteById(1);

        assertThatCode(() -> proposalService.removeProposal(1)).doesNotThrowAnyException();

        verify(proposalRepository).deleteById(1);
    }

    @Test
    @DisplayName("testGetAllProposals_ReturnsFullList")
    void testGetAllProposals_ReturnsFullList() {
        when(proposalRepository.findAll()).thenReturn(List.of(proposal));

        List<Proposal> result = proposalService.getAllProposals();

        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("testAddProposal_InvalidResourceReference_ThrowsException")
    void testAddProposal_InvalidResourceReference_ThrowsException() {
        when(resourceRepository.findById(1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> proposalService.addProposal(proposal))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Resource");

        verify(proposalRepository, never()).save(any());
    }
}
