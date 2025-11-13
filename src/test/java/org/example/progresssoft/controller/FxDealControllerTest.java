package org.example.progresssoft.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.progresssoft.dto.FxDealRequest;
import org.example.progresssoft.dto.FxDealResponse;
import org.example.progresssoft.exception.DuplicateDealException;
import org.example.progresssoft.exception.InvalidDealException;
import org.example.progresssoft.service.FxDealService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FxDealController.class)
@DisplayName("FxDealController Tests")
class FxDealControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private FxDealService fxDealService;

    private FxDealRequest validRequest;
    private FxDealResponse validResponse;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();

        validRequest = FxDealRequest.builder()
                .dealUniqueId("DEAL-001")
                .fromCurrencyIsoCode("USD")
                .toCurrencyIsoCode("EUR")
                .dealTimestamp(now.minusHours(1))
                .dealAmount(new BigDecimal("1000.5000"))
                .build();

        validResponse = FxDealResponse.builder()
                .id(1L)
                .dealUniqueId("DEAL-001")
                .fromCurrencyIsoCode("USD")
                .toCurrencyIsoCode("EUR")
                .dealTimestamp(now.minusHours(1))
                .dealAmount(new BigDecimal("1000.5000"))
                .createdAt(now)
                .build();
    }

    @Test
    @DisplayName("Should import deal successfully and return 201")
    void testImportDeal_Success() throws Exception {
        // Arrange
        when(fxDealService.importDeal(any(FxDealRequest.class))).thenReturn(validResponse);

        // Act & Assert
        mockMvc.perform(post("/api/fx-deals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.dealUniqueId").value("DEAL-001"))
                .andExpect(jsonPath("$.fromCurrencyIsoCode").value("USD"))
                .andExpect(jsonPath("$.toCurrencyIsoCode").value("EUR"))
                .andExpect(jsonPath("$.dealAmount").value(1000.5000));

        verify(fxDealService, times(1)).importDeal(any(FxDealRequest.class));
    }

    @Test
    @DisplayName("Should return 409 when importing duplicate deal")
    void testImportDeal_Duplicate() throws Exception {
        // Arrange
        when(fxDealService.importDeal(any(FxDealRequest.class)))
                .thenThrow(new DuplicateDealException("Deal with ID 'DEAL-001' already exists"));

        // Act & Assert
        mockMvc.perform(post("/api/fx-deals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Duplicate Deal"))
                .andExpect(jsonPath("$.message").value(containsString("already exists")));

        verify(fxDealService, times(1)).importDeal(any(FxDealRequest.class));
    }

    @Test
    @DisplayName("Should return 400 when validation fails - missing required fields")
    void testImportDeal_ValidationFailure_MissingFields() throws Exception {
        // Arrange
        FxDealRequest invalidRequest = FxDealRequest.builder()
                .dealUniqueId("")
                .fromCurrencyIsoCode("")
                .toCurrencyIsoCode("")
                .build();

        // Act & Assert
        mockMvc.perform(post("/api/fx-deals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation Failed"));

        verify(fxDealService, never()).importDeal(any(FxDealRequest.class));
    }

    @Test
    @DisplayName("Should return 400 when validation fails - invalid currency format")
    void testImportDeal_ValidationFailure_InvalidCurrencyFormat() throws Exception {
        // Arrange
        FxDealRequest invalidRequest = FxDealRequest.builder()
                .dealUniqueId("DEAL-002")
                .fromCurrencyIsoCode("US")
                .toCurrencyIsoCode("EURO")
                .dealTimestamp(LocalDateTime.now())
                .dealAmount(new BigDecimal("1000"))
                .build();

        // Act & Assert
        mockMvc.perform(post("/api/fx-deals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.validationErrors").exists());

        verify(fxDealService, never()).importDeal(any(FxDealRequest.class));
    }

    @Test
    @DisplayName("Should import batch deals and return 207")
    void testImportDeals_Batch() throws Exception {
        // Arrange
        List<FxDealRequest> requests = Arrays.asList(validRequest);

        FxDealService.DealImportResult successResult = 
                FxDealService.DealImportResult.success("DEAL-001", validResponse);

        when(fxDealService.importDeals(anyList()))
                .thenReturn(Collections.singletonList(successResult));

        // Act & Assert
        mockMvc.perform(post("/api/fx-deals/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requests)))
                .andExpect(status().isMultiStatus())
                .andExpect(jsonPath("$.totalReceived").value(1))
                .andExpect(jsonPath("$.successCount").value(1))
                .andExpect(jsonPath("$.failureCount").value(0))
                .andExpect(jsonPath("$.results").isArray());

        verify(fxDealService, times(1)).importDeals(anyList());
    }

    @Test
    @DisplayName("Should retrieve all deals with pagination")
    void testGetAllDeals() throws Exception {
        // Arrange
        Page<FxDealResponse> page = new PageImpl<>(Collections.singletonList(validResponse));
        when(fxDealService.getAllDeals(any(PageRequest.class))).thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/api/fx-deals")
                        .param("page", "0")
                        .param("size", "20")
                        .param("sortBy", "createdAt")
                        .param("sortDir", "desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].dealUniqueId").value("DEAL-001"));

        verify(fxDealService, times(1)).getAllDeals(any(PageRequest.class));
    }

    @Test
    @DisplayName("Should retrieve deal by unique ID")
    void testGetDealByUniqueId() throws Exception {
        // Arrange
        when(fxDealService.getDealByUniqueId("DEAL-001")).thenReturn(validResponse);

        // Act & Assert
        mockMvc.perform(get("/api/fx-deals/DEAL-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dealUniqueId").value("DEAL-001"))
                .andExpect(jsonPath("$.fromCurrencyIsoCode").value("USD"));

        verify(fxDealService, times(1)).getDealByUniqueId("DEAL-001");
    }

    @Test
    @DisplayName("Should return 400 when deal not found")
    void testGetDealByUniqueId_NotFound() throws Exception {
        // Arrange
        when(fxDealService.getDealByUniqueId("NONEXISTENT"))
                .thenThrow(new InvalidDealException("Deal not found"));

        // Act & Assert
        mockMvc.perform(get("/api/fx-deals/NONEXISTENT"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Deal not found"));

        verify(fxDealService, times(1)).getDealByUniqueId("NONEXISTENT");
    }

    @Test
    @DisplayName("Should return health status")
    void testHealth() throws Exception {
        mockMvc.perform(get("/api/fx-deals/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.service").value("FX Deals Warehouse"));
    }
}
