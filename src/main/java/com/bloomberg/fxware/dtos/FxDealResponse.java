package com.bloomberg.fxware.dtos;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class FxDealResponse {
    private String dealUniqueId;
    private boolean success;
    private String message;
}
