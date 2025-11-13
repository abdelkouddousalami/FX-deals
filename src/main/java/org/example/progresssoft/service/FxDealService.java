package org.example.progresssoft.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.progresssoft.dto.FxDealRequest;
import org.example.progresssoft.dto.FxDealResponse;
import org.example.progresssoft.entity.FxDeal;
import org.example.progresssoft.exception.DuplicateDealException;
import org.example.progresssoft.exception.InvalidDealException;
import org.example.progresssoft.repository.FxDealRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.Currency;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service layer for FX Deal operations.
 * Handles business logic, validation, and persistence.
 */
@Service
@Validated
@RequiredArgsConstructor
@Slf4j
public class FxDealService {

    private final FxDealRepository fxDealRepository;

    /**
     * Import a single FX deal into the system.
     * Validates the deal and ensures no duplicates are imported.
     *
     * @param request the deal request containing deal details
     * @return the persisted deal response
     * @throws DuplicateDealException if deal already exists
     * @throws InvalidDealException if deal validation fails
     */
    @Transactional
    public FxDealResponse importDeal(@Valid FxDealRequest request) {
        log.info("Attempting to import deal with ID: {}", request.getDealUniqueId());

        // Duplicate check at database level ensures data integrity
        if (fxDealRepository.existsByDealUniqueId(request.getDealUniqueId())) {
            log.warn("Duplicate deal detected: {}", request.getDealUniqueId());
            throw new DuplicateDealException(
                    String.format("Deal with ID '%s' already exists in the system", request.getDealUniqueId())
            );
        }

        // Additional business validations beyond bean validation
        validateCurrencies(request);
        validateCurrencyPair(request);

        // Convert DTO to Entity
        FxDeal deal = FxDeal.builder()
                .dealUniqueId(request.getDealUniqueId())
                .fromCurrencyIsoCode(request.getFromCurrencyIsoCode())
                .toCurrencyIsoCode(request.getToCurrencyIsoCode())
                .dealTimestamp(request.getDealTimestamp())
                .dealAmount(request.getDealAmount())
                .build();

        // Persist the deal
        FxDeal savedDeal = fxDealRepository.save(deal);
        log.info("Successfully imported deal with ID: {} and database ID: {}", 
                savedDeal.getDealUniqueId(), savedDeal.getId());

        return mapToResponse(savedDeal);
    }

    /**
     * Import multiple FX deals in batch.
     * Each deal is processed independently - failures do not rollback successful imports.
     *
     * @param requests list of deal requests
     * @return list of import results with success/failure status
     */
    public List<DealImportResult> importDeals(List<@Valid FxDealRequest> requests) {
        log.info("Starting batch import of {} deals", requests.size());

        // Process each deal independently without transaction rollback
        // This ensures successful deals are persisted even if others fail
        return requests.stream()
                .map(this::processSingleDeal)
                .collect(Collectors.toList());
    }

    /**
     * Process a single deal independently (no rollback on failure).
     * Each deal is wrapped in its own transaction via importDeal(),
     * so failures are isolated and don't affect other deals in the batch.
     */
    private DealImportResult processSingleDeal(FxDealRequest request) {
        try {
            FxDealResponse response = importDeal(request);
            return DealImportResult.success(request.getDealUniqueId(), response);
        } catch (DuplicateDealException | InvalidDealException ex) {
            log.warn("Failed to import deal {}: {}", request.getDealUniqueId(), ex.getMessage());
            return DealImportResult.failure(request.getDealUniqueId(), ex.getMessage());
        } catch (Exception ex) {
            log.error("Unexpected error importing deal {}: ", request.getDealUniqueId(), ex);
            return DealImportResult.failure(request.getDealUniqueId(), 
                    "Unexpected error: " + ex.getMessage());
        }
    }

    /**
     * Retrieve all deals with pagination support.
     */
    @Transactional(readOnly = true)
    public Page<FxDealResponse> getAllDeals(Pageable pageable) {
        log.debug("Fetching deals with pagination: {}", pageable);
        return fxDealRepository.findAll(pageable)
                .map(this::mapToResponse);
    }

    /**
     * Retrieve a deal by its unique ID.
     */
    @Transactional(readOnly = true)
    public FxDealResponse getDealByUniqueId(String dealUniqueId) {
        log.debug("Fetching deal with unique ID: {}", dealUniqueId);
        return fxDealRepository.findByDealUniqueId(dealUniqueId)
                .map(this::mapToResponse)
                .orElseThrow(() -> new InvalidDealException(
                        String.format("Deal with ID '%s' not found", dealUniqueId)
                ));
    }

    /**
     * Validate that the currency codes are valid ISO 4217 codes.
     * Currency.getInstance() throws IllegalArgumentException for invalid codes.
     */
    private void validateCurrencies(FxDealRequest request) {
        try {
            Currency.getInstance(request.getFromCurrencyIsoCode());
            Currency.getInstance(request.getToCurrencyIsoCode());
        } catch (IllegalArgumentException ex) {
            log.error("Invalid currency code provided: {}", ex.getMessage());
            throw new InvalidDealException("Invalid ISO currency code provided");
        }
    }

    /**
     * Validate that the from and to currencies are different.
     */
    private void validateCurrencyPair(FxDealRequest request) {
        if (request.getFromCurrencyIsoCode().equals(request.getToCurrencyIsoCode())) {
            log.error("Same currency pair detected: {}", request.getFromCurrencyIsoCode());
            throw new InvalidDealException(
                    "From and To currencies must be different for an FX deal"
            );
        }
    }

    /**
     * Map entity to response DTO.
     */
    private FxDealResponse mapToResponse(FxDeal deal) {
        return FxDealResponse.builder()
                .id(deal.getId())
                .dealUniqueId(deal.getDealUniqueId())
                .fromCurrencyIsoCode(deal.getFromCurrencyIsoCode())
                .toCurrencyIsoCode(deal.getToCurrencyIsoCode())
                .dealTimestamp(deal.getDealTimestamp())
                .dealAmount(deal.getDealAmount())
                .createdAt(deal.getCreatedAt())
                .build();
    }

    /**
     * Result object for batch import operations.
     */
    @lombok.Data
    @lombok.AllArgsConstructor
    public static class DealImportResult {
        private String dealUniqueId;
        private boolean success;
        private String message;
        private FxDealResponse data;

        public static DealImportResult success(String dealUniqueId, FxDealResponse data) {
            return new DealImportResult(dealUniqueId, true, "Deal imported successfully", data);
        }

        public static DealImportResult failure(String dealUniqueId, String errorMessage) {
            return new DealImportResult(dealUniqueId, false, errorMessage, null);
        }
    }
}
