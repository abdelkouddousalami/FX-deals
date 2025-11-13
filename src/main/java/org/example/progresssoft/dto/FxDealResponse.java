package org.example.progresssoft.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for FX Deal responses.
 * Used for sending deal information to clients.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "FX Deal response")
public class FxDealResponse {

    @Schema(description = "Database ID of the deal", example = "1")
    private Long id;

    @Schema(description = "Unique identifier for the deal", example = "DEAL-001")
    private String dealUniqueId;

    @Schema(description = "Source currency ISO code", example = "USD")
    private String fromCurrencyIsoCode;

    @Schema(description = "Target currency ISO code", example = "EUR")
    private String toCurrencyIsoCode;

    @Schema(description = "Timestamp when the deal was executed", example = "2024-11-13T10:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dealTimestamp;

    @Schema(description = "Deal amount in the source currency", example = "1000000.50")
    private BigDecimal dealAmount;

    @Schema(description = "Timestamp when the deal was imported to the system", example = "2024-11-13T15:45:00")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
}
