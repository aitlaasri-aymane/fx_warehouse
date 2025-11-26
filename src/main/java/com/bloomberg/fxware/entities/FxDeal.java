package com.bloomberg.fxware.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "fx_deals")
@Data
public class FxDeal {

    @Id
    @Column(name = "deal_unique_id", nullable = false)
    private String dealUniqueId;

    @Column(name = "from_currency_iso", nullable = false, length = 3)
    private String fromCurrencyIso;

    @Column(name = "to_currency_iso", nullable = false, length = 3)
    private String toCurrencyIso;

    @Column(name = "deal_timestamp", nullable = false)
    private LocalDateTime dealTimestamp;

    @Column(name = "deal_amount", nullable = false)
    private BigDecimal dealAmount;
}
