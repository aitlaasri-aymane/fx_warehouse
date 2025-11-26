package com.bloomberg.fxware.services;

import com.bloomberg.fxware.dtos.FxDealRequest;
import com.bloomberg.fxware.dtos.FxDealResponse;

import java.util.List;

public interface FxDealService {
    List<FxDealResponse> importDeals(List<FxDealRequest> dealRequests);
}
