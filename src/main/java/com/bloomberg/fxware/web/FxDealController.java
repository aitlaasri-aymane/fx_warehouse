package com.bloomberg.fxware.web;

import com.bloomberg.fxware.dtos.FxDealRequest;
import com.bloomberg.fxware.dtos.FxDealResponse;
import com.bloomberg.fxware.services.FxDealService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/deals")
@RequiredArgsConstructor
@Validated
@Tag(name = "FX Deal Importing", description = "APIs for Importing FX deals")
public class FxDealController {
    private final FxDealService service;

    @Operation(summary = "Import FX Deals", description = "Imports a list of FX deals into the DB")
    @PostMapping("/import")
    public ResponseEntity<List<FxDealResponse>> importDeals(@RequestBody List<FxDealRequest> deals) {
        return ResponseEntity.ok(service.importDeals(deals));
    }
}
