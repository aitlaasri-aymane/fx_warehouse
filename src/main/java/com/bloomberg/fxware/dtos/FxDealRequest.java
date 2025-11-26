package com.bloomberg.fxware.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class FxDealRequest {
    @NotNull(message = "Deal Unique Id is required")
    private String dealUniqueId;

    @NotNull(message = "From Currency is required")
    @Pattern(regexp = "^[A-Z]{3}$", message = "From Currency must be a 3-letter ISO code")
    private String fromCurrencyIso;

    @NotNull(message = "To Currency is required")
    @Pattern(regexp = "^[A-Z]{3}$", message = "To Currency must be a 3-letter ISO code")
    private String toCurrencyIso;

    @NotNull(message = "Timestamp is required")
    private LocalDateTime dealTimestamp;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than 0")
    private BigDecimal dealAmount;
}
