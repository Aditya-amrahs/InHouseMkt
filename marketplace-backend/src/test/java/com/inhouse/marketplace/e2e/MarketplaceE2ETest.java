package com.inhouse.marketplace.e2e;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inhouse.marketplace.entity.Employee;
import com.inhouse.marketplace.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.mock.web.MockHttpSession;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * End-to-end tests using MockMvc + full Spring Boot context + H2.
 * Tests the complete API request/response flow for INT-001.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@DisplayName("End-to-End API Integration Tests")
class MarketplaceE2ETest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // -----------------------------------------------------------------------
    // E2E: Registration + Login
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("testE2E_EmployeeRegistersAndLogsIn")
    void testE2E_EmployeeRegistersAndLogsIn() throws Exception {
        User user = User.builder().userId("alice@company.com").password("Pass@123").build();

        // Step 1 — Register
        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value("alice@company.com"));

        // Step 2 — Login with correct credentials
        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value("alice@company.com"));

        // Step 3 — Login with wrong password should fail
        User wrongPass = User.builder().userId("alice@company.com").password("wrong").build();
        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(wrongPass)))
                .andExpect(status().isUnauthorized());
    }

    // -----------------------------------------------------------------------
    // E2E: Requirement → Proposal → Acceptance
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("testE2E_RequirementToProposalToAcceptance")
    void testE2E_RequirementToProposalToAcceptance() throws Exception {
        // Setup users & employees
        User u1 = User.builder().userId("bob@company.com").password("pass").build();
        User u2 = User.builder().userId("carol@company.com").password("pass").build();

        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(u1))).andExpect(status().isCreated());
        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(u2))).andExpect(status().isCreated());

        // Create employee for bob
        Employee emp1 = Employee.builder().empName("Bob").deptName("IT")
                .location("Pune").user(u1).build();
        MvcResult empResult = mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emp1)))
                .andExpect(status().isCreated()).andReturn();
        Employee savedEmp = objectMapper.readValue(empResult.getResponse().getContentAsString(), Employee.class);

        // Create employee for carol
        Employee emp2 = Employee.builder().empName("Carol").deptName("HR")
                .location("Mumbai").user(u2).build();
        MvcResult emp2Result = mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emp2)))
                .andExpect(status().isCreated()).andReturn();
        Employee savedEmp2 = objectMapper.readValue(emp2Result.getResponse().getContentAsString(), Employee.class);

        // Bob posts a requirement
        String reqJson = """
            {
              "title": "Looking for PG near office",
              "category": "Accommodation",
              "type": "RENT",
              "price": 8000,
              "emp": {"empId": %d}
            }
            """.formatted(savedEmp.getEmpId());

        MvcResult reqResult = mockMvc.perform(post("/api/requirements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reqJson))
                .andExpect(status().isCreated()).andReturn();

        int reqId = objectMapper.readTree(reqResult.getResponse().getContentAsString())
                .get("resId").asInt();

        // Carol submits a proposal
        String propJson = """
            {
              "proposal": "I have a 2BHK flat nearby",
              "amount": 9000,
              "emp": {"empId": %d},
              "resource": {"resId": %d}
            }
            """.formatted(savedEmp2.getEmpId(), reqId);

        MvcResult propResult = mockMvc.perform(post("/api/proposals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(propJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accepted").value(false))
                .andReturn();

        int propId = objectMapper.readTree(propResult.getResponse().getContentAsString())
                .get("propId").asInt();

        // Bob accepts Carol's proposal
        String acceptJson = """
            {"propId": %d}
            """.formatted(propId);

        mockMvc.perform(patch("/api/employees/proposals/accepted")
                        .session(sessionFor("bob@company.com"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(acceptJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accepted").value(true));
    }

    // -----------------------------------------------------------------------
    // E2E: Offer → Proposal → Acceptance
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("testE2E_OfferToProposalToAcceptance")
    void testE2E_OfferToProposalToAcceptance() throws Exception {
        // Setup users & employees
        User u1 = User.builder().userId("eve@company.com").password("pass").build();
        User u2 = User.builder().userId("frank@company.com").password("pass").build();

        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(u1))).andExpect(status().isCreated());
        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(u2))).andExpect(status().isCreated());

        Employee emp1 = Employee.builder().empName("Eve").deptName("Sales")
                .location("Delhi").user(u1).build();
        MvcResult empResult = mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emp1)))
                .andExpect(status().isCreated()).andReturn();
        Employee savedEmp = objectMapper.readValue(empResult.getResponse().getContentAsString(), Employee.class);

        Employee emp2 = Employee.builder().empName("Frank").deptName("Finance")
                .location("Chennai").user(u2).build();
        MvcResult emp2Result = mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emp2)))
                .andExpect(status().isCreated()).andReturn();
        Employee savedEmp2 = objectMapper.readValue(emp2Result.getResponse().getContentAsString(), Employee.class);

        // Eve posts an offer — service marks it available automatically
        String offerJson = """
            {
              "title": "Selling used bike",
              "category": "Vehicle",
              "type": "SELL",
              "price": 45000,
              "emp": {"empId": %d}
            }
            """.formatted(savedEmp.getEmpId());

        MvcResult offerResult = mockMvc.perform(post("/api/offers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(offerJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.available").value(true))
                .andReturn();

        int offerId = objectMapper.readTree(offerResult.getResponse().getContentAsString())
                .get("resId").asInt();

        // Frank submits a proposal against the offer
        String propJson = """
            {
              "proposal": "Interested, can pay 42000",
              "amount": 42000,
              "emp": {"empId": %d},
              "resource": {"resId": %d}
            }
            """.formatted(savedEmp2.getEmpId(), offerId);

        MvcResult propResult = mockMvc.perform(post("/api/proposals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(propJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accepted").value(false))
                .andReturn();

        int propId = objectMapper.readTree(propResult.getResponse().getContentAsString())
                .get("propId").asInt();

        // Eve accepts Frank's proposal
        String acceptJson = """
            {"propId": %d}
            """.formatted(propId);

        mockMvc.perform(patch("/api/employees/proposals/accepted")
                        .session(sessionFor("eve@company.com"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(acceptJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accepted").value(true));

        // Eve then marks the offer unavailable
        String availabilityJson = """
            {"resId": %d, "available": false}
            """.formatted(offerId);

        mockMvc.perform(patch("/api/employees/offers/availability")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(availabilityJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available").value(false));
    }

    // -----------------------------------------------------------------------
    // E2E: UI-to-API integration (CORS contract used by the Angular app)
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("testE2E_UIToAPIIntegration")
    void testE2E_UIToAPIIntegration() throws Exception {
        // Preflight request sent by the React dev server.
        mockMvc.perform(options("/api/offers")
                        .header("Origin", "http://localhost:5173")
                        .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:5173"));

        // Actual cross-origin GET reflects the allowed origin and returns JSON
        mockMvc.perform(get("/api/offers")
                        .header("Origin", "http://localhost:5173"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:5173"))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        // A disallowed origin is rejected
        mockMvc.perform(get("/api/offers")
                        .header("Origin", "http://evil.example.com"))
                .andExpect(status().isForbidden());
    }

    // -----------------------------------------------------------------------
    // E2E: Delete cascades correctly
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("testE2E_DeleteCascadesCorrectly")
    void testE2E_DeleteCascadesCorrectly() throws Exception {
        User u = User.builder().userId("dave@company.com").password("pass").build();
        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(u))).andExpect(status().isCreated());

        Employee emp = Employee.builder().empName("Dave").deptName("Dev").location("Bangalore").user(u).build();
        MvcResult empResult = mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emp)))
                .andExpect(status().isCreated()).andReturn();
        Employee savedEmp = objectMapper.readValue(empResult.getResponse().getContentAsString(), Employee.class);

        String reqJson = """
            {"title":"Temp","category":"Other","type":"FREE","price":0,"emp":{"empId":%d}}
            """.formatted(savedEmp.getEmpId());

        MvcResult reqResult = mockMvc.perform(post("/api/requirements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reqJson))
                .andExpect(status().isCreated()).andReturn();
        int reqId = objectMapper.readTree(reqResult.getResponse().getContentAsString()).get("resId").asInt();

        // Add proposal
        String propJson = """
            {"proposal":"test","amount":0,"emp":{"empId":%d},"resource":{"resId":%d}}
            """.formatted(savedEmp.getEmpId(), reqId);
        mockMvc.perform(post("/api/proposals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(propJson)).andExpect(status().isCreated());

        // Delete requirement — should cascade delete proposals
        mockMvc.perform(delete("/api/requirements/" + reqId))
                .andExpect(status().isNoContent());

        // Requirement should be gone
        mockMvc.perform(get("/api/requirements/" + reqId))
                .andExpect(status().isNotFound());
    }

    private MockHttpSession sessionFor(String userId) {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("AUTH_USER_ID", userId);
        return session;
    }
}
