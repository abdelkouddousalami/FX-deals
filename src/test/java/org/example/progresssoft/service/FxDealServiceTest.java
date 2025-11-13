package org.example.progresssoft.service;

import org.example.progresssoft.dto.FxDealRequest;
import org.example.progresssoft.dto.FxDealResponse;
import org.example.progresssoft.entity.FxDeal;
import org.example.progresssoft.exception.DuplicateDealException;
import org.example.progresssoft.exception.InvalidDealException;
import org.example.progresssoft.repository.FxDealRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("FxDealService Tests")
class FxDealServiceTest {

    @Mock
    private FxDealRepository fxDealRepository;

    @InjectMocks
    private FxDealService fxDealService;

    private FxDealRequest validRequest;
    private FxDeal validDeal;

    @BeforeEach
    void setUp() {
        validRequest = FxDealRequest.builder()
                .dealUniqueId("DEAL-001")
                .fromCurrencyIsoCode("USD")
                .toCurrencyIsoCode("EUR")
                .dealTimestamp(LocalDateTime.now().minusHours(1))
                .dealAmount(new BigDecimal("1000.5000"))
                .build();

        validDeal = FxDeal.builder()
                .id(1L)
                .dealUniqueId("DEAL-001")
                .fromCurrencyIsoCode("USD")
                .toCurrencyIsoCode("EUR")
                .dealTimestamp(validRequest.getDealTimestamp())
                .dealAmount(validRequest.getDealAmount())
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Should successfully import a valid deal")
    void testImportDeal_Success() {
        // Arrange
        when(fxDealRepository.existsByDealUniqueId(validRequest.getDealUniqueId())).thenReturn(false);
        when(fxDealRepository.save(any(FxDeal.class))).thenReturn(validDeal);

        // Act
        FxDealResponse response = fxDealService.importDeal(validRequest);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getDealUniqueId()).isEqualTo("DEAL-001");
        assertThat(response.getFromCurrencyIsoCode()).isEqualTo("USD");
        assertThat(response.getToCurrencyIsoCode()).isEqualTo("EUR");
        assertThat(response.getDealAmount()).isEqualByComparingTo(new BigDecimal("1000.5000"));

        verify(fxDealRepository, times(1)).existsByDealUniqueId("DEAL-001");
        verify(fxDealRepository, times(1)).save(any(FxDeal.class));
    }

    @Test
    @DisplayName("Should throw DuplicateDealException when deal already exists")
    void testImportDeal_Duplicate() {
        // Arrange
        when(fxDealRepository.existsByDealUniqueId(validRequest.getDealUniqueId())).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> fxDealService.importDeal(validRequest))
                .isInstanceOf(DuplicateDealException.class)
                .hasMessageContaining("already exists");

        verify(fxDealRepository, times(1)).existsByDealUniqueId("DEAL-001");
        verify(fxDealRepository, never()).save(any(FxDeal.class));
    }

    @Test
    @DisplayName("Should throw InvalidDealException when currencies are invalid")
    void testImportDeal_InvalidCurrency() {
        // Arrange
        FxDealRequest invalidRequest = FxDealRequest.builder()
                .dealUniqueId("DEAL-002")
                .fromCurrencyIsoCode("ZZZ")
                .toCurrencyIsoCode("EUR")
                .dealTimestamp(LocalDateTime.now())
                .dealAmount(new BigDecimal("1000"))
                .build();

        when(fxDealRepository.existsByDealUniqueId(invalidRequest.getDealUniqueId())).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> fxDealService.importDeal(invalidRequest))
                .isInstanceOf(InvalidDealException.class)
                .hasMessageContaining("Invalid ISO currency code");

        verify(fxDealRepository, times(1)).existsByDealUniqueId("DEAL-002");
        verify(fxDealRepository, never()).save(any(FxDeal.class));
    }

    @Test
    @DisplayName("Should throw InvalidDealException when from and to currencies are the same")
    void testImportDeal_SameCurrency() {
        // Arrange
        FxDealRequest sameRequest = FxDealRequest.builder()
                .dealUniqueId("DEAL-003")
                .fromCurrencyIsoCode("USD")
                .toCurrencyIsoCode("USD")
                .dealTimestamp(LocalDateTime.now())
                .dealAmount(new BigDecimal("1000"))
                .build();

        when(fxDealRepository.existsByDealUniqueId(sameRequest.getDealUniqueId())).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> fxDealService.importDeal(sameRequest))
                .isInstanceOf(InvalidDealException.class)
                .hasMessageContaining("must be different");

        verify(fxDealRepository, never()).save(any(FxDeal.class));
    }

    @Test
    @DisplayName("Should import multiple deals in batch")
    void testImportDeals_Batch() {
        // Arrange
        FxDealRequest request1 = FxDealRequest.builder()
                .dealUniqueId("DEAL-B1")
                .fromCurrencyIsoCode("USD")
                .toCurrencyIsoCode("EUR")
                .dealTimestamp(LocalDateTime.now())
                .dealAmount(new BigDecimal("1000"))
                .build();

        FxDealRequest request2 = FxDealRequest.builder()
                .dealUniqueId("DEAL-B2")
                .fromCurrencyIsoCode("GBP")
                .toCurrencyIsoCode("JPY")
                .dealTimestamp(LocalDateTime.now())
                .dealAmount(new BigDecimal("2000"))
                .build();

        FxDeal deal1 = FxDeal.builder().id(1L).dealUniqueId("DEAL-B1")
                .fromCurrencyIsoCode("USD").toCurrencyIsoCode("EUR")
                .dealTimestamp(request1.getDealTimestamp())
                .dealAmount(request1.getDealAmount()).createdAt(LocalDateTime.now()).build();

        FxDeal deal2 = FxDeal.builder().id(2L).dealUniqueId("DEAL-B2")
                .fromCurrencyIsoCode("GBP").toCurrencyIsoCode("JPY")
                .dealTimestamp(request2.getDealTimestamp())
                .dealAmount(request2.getDealAmount()).createdAt(LocalDateTime.now()).build();

        when(fxDealRepository.existsByDealUniqueId("DEAL-B1")).thenReturn(false);
        when(fxDealRepository.existsByDealUniqueId("DEAL-B2")).thenReturn(false);
        when(fxDealRepository.save(any(FxDeal.class))).thenReturn(deal1, deal2);

        // Act
        List<FxDealService.DealImportResult> results = 
                fxDealService.importDeals(Arrays.asList(request1, request2));

        // Assert
        assertThat(results).hasSize(2);
        assertThat(results.stream().filter(FxDealService.DealImportResult::isSuccess).count()).isEqualTo(2);
    }

    @Test
    @DisplayName("Should handle mixed success and failure in batch import")
    void testImportDeals_PartialSuccess() {
        // Arrange
        FxDealRequest validReq = FxDealRequest.builder()
                .dealUniqueId("DEAL-V1")
                .fromCurrencyIsoCode("USD")
                .toCurrencyIsoCode("EUR")
                .dealTimestamp(LocalDateTime.now())
                .dealAmount(new BigDecimal("1000"))
                .build();

        FxDealRequest duplicateReq = FxDealRequest.builder()
                .dealUniqueId("DEAL-DUP")
                .fromCurrencyIsoCode("GBP")
                .toCurrencyIsoCode("JPY")
                .dealTimestamp(LocalDateTime.now())
                .dealAmount(new BigDecimal("2000"))
                .build();

        FxDeal deal1 = FxDeal.builder().id(1L).dealUniqueId("DEAL-V1")
                .fromCurrencyIsoCode("USD").toCurrencyIsoCode("EUR")
                .dealTimestamp(validReq.getDealTimestamp())
                .dealAmount(validReq.getDealAmount()).createdAt(LocalDateTime.now()).build();

        when(fxDealRepository.existsByDealUniqueId("DEAL-V1")).thenReturn(false);
        when(fxDealRepository.existsByDealUniqueId("DEAL-DUP")).thenReturn(true);
        when(fxDealRepository.save(any(FxDeal.class))).thenReturn(deal1);

        // Act
        List<FxDealService.DealImportResult> results = 
                fxDealService.importDeals(Arrays.asList(validReq, duplicateReq));

        // Assert
        assertThat(results).hasSize(2);
        assertThat(results.stream().filter(FxDealService.DealImportResult::isSuccess).count()).isEqualTo(1);
        assertThat(results.stream().filter(r -> !r.isSuccess()).count()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should retrieve all deals with pagination")
    void testGetAllDeals() {
        // Arrange
        List<FxDeal> deals = Arrays.asList(validDeal);
        Page<FxDeal> dealPage = new PageImpl<>(deals);
        Pageable pageable = PageRequest.of(0, 10);

        when(fxDealRepository.findAll(pageable)).thenReturn(dealPage);

        // Act
        Page<FxDealResponse> result = fxDealService.getAllDeals(pageable);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getDealUniqueId()).isEqualTo("DEAL-001");

        verify(fxDealRepository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("Should retrieve deal by unique ID")
    void testGetDealByUniqueId_Found() {
        // Arrange
        when(fxDealRepository.findByDealUniqueId("DEAL-001")).thenReturn(Optional.of(validDeal));

        // Act
        FxDealResponse response = fxDealService.getDealByUniqueId("DEAL-001");

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getDealUniqueId()).isEqualTo("DEAL-001");

        verify(fxDealRepository, times(1)).findByDealUniqueId("DEAL-001");
    }

    @Test
    @DisplayName("Should throw InvalidDealException when deal not found")
    void testGetDealByUniqueId_NotFound() {
        // Arrange
        when(fxDealRepository.findByDealUniqueId("NONEXISTENT")).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> fxDealService.getDealByUniqueId("NONEXISTENT"))
                .isInstanceOf(InvalidDealException.class)
                .hasMessageContaining("not found");

        verify(fxDealRepository, times(1)).findByDealUniqueId("NONEXISTENT");
    }
}
