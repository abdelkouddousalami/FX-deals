package org.example.progresssoft.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for FX Deal requests.
 * Used for receiving deal information from clients.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "FX Deal import request")
public class FxDealRequest {

    @Schema(description = "Unique identifier for the deal", example = "DEAL-001", required = true)
    @NotBlank(message = "Deal Unique ID is required")
    @Size(max = 100, message = "Deal Unique ID must not exceed 100 characters")
    private String dealUniqueId;

    @Schema(description = "Source currency ISO 4217 code (3 letters)", example = "USD", required = true)
    @NotBlank(message = "From Currency ISO Code is required")
    @Pattern(regexp = "^[A-Z]{3}$", message = "From Currency must be a valid 3-letter ISO code (e.g., USD, EUR)")
    private String fromCurrencyIsoCode;

    @Schema(description = "Target currency ISO 4217 code (3 letters)", example = "EUR", required = true)
    @NotBlank(message = "To Currency ISO Code is required")
    @Pattern(regexp = "^[A-Z]{3}$", message = "To Currency must be a valid 3-letter ISO code (e.g., USD, EUR)")
    private String toCurrencyIsoCode;

    @Schema(description = "Timestamp when the deal was executed", example = "2024-11-13T10:30:00", required = true)
    @NotNull(message = "Deal timestamp is required")
    @PastOrPresent(message = "Deal timestamp cannot be in the future")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dealTimestamp;

    @Schema(description = "Deal amount in the source currency", example = "1000000.50", required = true)
    @NotNull(message = "Deal amount is required")
    @DecimalMin(value = "0.0001", inclusive = true, message = "Deal amount must be greater than 0")
    @Digits(integer = 15, fraction = 4, message = "Deal amount must have at most 15 integer digits and 4 decimal places")
    private BigDecimal dealAmount;
}
