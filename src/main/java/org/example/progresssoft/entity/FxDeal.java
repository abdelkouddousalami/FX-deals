package org.example.progresssoft.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity representing an FX Deal in the data warehouse.
 * Stores foreign exchange transaction details with validation constraints.
 */
@Entity
@Table(name = "fx_deals", indexes = {
        @Index(name = "idx_deal_unique_id", columnList = "dealUniqueId", unique = true),
        @Index(name = "idx_deal_timestamp", columnList = "dealTimestamp")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FxDeal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    @NotBlank(message = "Deal Unique ID is required")
    @Size(max = 100, message = "Deal Unique ID must not exceed 100 characters")
    private String dealUniqueId;

    @Column(nullable = false, length = 3)
    @NotBlank(message = "From Currency ISO Code is required")
    @Pattern(regexp = "^[A-Z]{3}$", message = "From Currency must be a valid 3-letter ISO code")
    private String fromCurrencyIsoCode;

    @Column(nullable = false, length = 3)
    @NotBlank(message = "To Currency ISO Code is required")
    @Pattern(regexp = "^[A-Z]{3}$", message = "To Currency must be a valid 3-letter ISO code")
    private String toCurrencyIsoCode;

    @Column(nullable = false)
    @NotNull(message = "Deal timestamp is required")
    @PastOrPresent(message = "Deal timestamp cannot be in the future")
    private LocalDateTime dealTimestamp;

    @Column(nullable = false, precision = 19, scale = 4)
    @NotNull(message = "Deal amount is required")
    @DecimalMin(value = "0.0001", inclusive = true, message = "Deal amount must be greater than 0")
    @Digits(integer = 15, fraction = 4, message = "Deal amount must have at most 15 integer digits and 4 decimal places")
    private BigDecimal dealAmount;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
