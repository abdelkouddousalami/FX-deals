package org.example.progresssoft.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.progresssoft.dto.FxDealRequest;
import org.example.progresssoft.dto.FxDealResponse;
import org.example.progresssoft.exception.ErrorResponse;
import org.example.progresssoft.service.FxDealService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/fx-deals")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "FX Deals", description = "Foreign Exchange Deals Management API")
public class FxDealController {

    private final FxDealService fxDealService;

    /**
     * Import a single FX deal.
     *
     * @param request the deal request
     * @return the imported deal response
     */
    @Operation(
            summary = "Import a single FX deal",
            description = "Import and persist a single foreign exchange deal with validation and duplicate detection"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Deal imported successfully",
                    content = @Content(schema = @Schema(implementation = FxDealResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid deal data",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Duplicate deal - already exists",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<FxDealResponse> importDeal(@Valid @RequestBody FxDealRequest request) {
        log.info("Received request to import deal: {}", request.getDealUniqueId());
        FxDealResponse response = fxDealService.importDeal(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Import multiple FX deals in batch.
     * Each deal is processed independently without rollback.
     *
     * @param requests list of deal requests
     * @return batch import results
     */
    @Operation(
            summary = "Batch import FX deals",
            description = "Import multiple FX deals simultaneously. Each deal is processed independently - successful deals are persisted even if others fail (no rollback policy)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "207", description = "Multi-status - batch processing completed with individual results"),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/batch")
    public ResponseEntity<Map<String, Object>> importDeals(
            @Valid @RequestBody List<@Valid FxDealRequest> requests) {
        log.info("Received batch import request with {} deals", requests.size());

        List<FxDealService.DealImportResult> results = fxDealService.importDeals(requests);

        long successCount = results.stream().filter(FxDealService.DealImportResult::isSuccess).count();
        long failureCount = results.size() - successCount;

        Map<String, Object> response = new HashMap<>();
        response.put("totalReceived", requests.size());
        response.put("successCount", successCount);
        response.put("failureCount", failureCount);
        response.put("results", results);

        log.info("Batch import completed: {} successful, {} failed", successCount, failureCount);

        return ResponseEntity.status(HttpStatus.MULTI_STATUS).body(response);
    }

    /**
     * Retrieve all FX deals with pagination.
     *
     * @param page page number (default: 0)
     * @param size page size (default: 20)
     * @param sortBy field to sort by (default: createdAt)
     * @param sortDir sort direction (default: desc)
     * @return paginated deals
     */
    @Operation(
            summary = "Get all FX deals",
            description = "Retrieve all FX deals with pagination and sorting support"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved deals")
    })
    @GetMapping
    public ResponseEntity<Page<FxDealResponse>> getAllDeals(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Field to sort by") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)") @RequestParam(defaultValue = "desc") String sortDir) {

        Sort.Direction direction = sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        log.debug("Fetching deals - page: {}, size: {}, sortBy: {}, sortDir: {}", 
                page, size, sortBy, sortDir);

        Page<FxDealResponse> deals = fxDealService.getAllDeals(pageable);
        return ResponseEntity.ok(deals);
    }

    /**
     * Retrieve a specific deal by its unique ID.
     *
     * @param dealUniqueId the unique identifier of the deal
     * @return the deal response
     */
    @Operation(
            summary = "Get FX deal by unique ID",
            description = "Retrieve a specific FX deal using its unique identifier"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Deal found",
                    content = @Content(schema = @Schema(implementation = FxDealResponse.class))),
            @ApiResponse(responseCode = "404", description = "Deal not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{dealUniqueId}")
    public ResponseEntity<FxDealResponse> getDealByUniqueId(
            @Parameter(description = "Unique identifier of the deal") @PathVariable String dealUniqueId) {
        log.debug("Fetching deal with unique ID: {}", dealUniqueId);
        FxDealResponse response = fxDealService.getDealByUniqueId(dealUniqueId);
        return ResponseEntity.ok(response);
    }

    /**
     * Health check endpoint.
     *
     * @return health status
     */
    @Operation(
            summary = "Health check",
            description = "Check if the FX Deals API service is running"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Service is healthy")
    })
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "FX Deals Warehouse");
        return ResponseEntity.ok(health);
    }
}
