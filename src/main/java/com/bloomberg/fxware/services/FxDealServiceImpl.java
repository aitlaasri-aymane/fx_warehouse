package com.bloomberg.fxware.services;

import com.bloomberg.fxware.dtos.FxDealRequest;
import com.bloomberg.fxware.dtos.FxDealResponse;
import com.bloomberg.fxware.entities.FxDeal;
import com.bloomberg.fxware.mappers.FxDealMapper;
import com.bloomberg.fxware.repositories.FxDealRepo;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FxDealServiceImpl implements FxDealService {

    private final FxDealRepo repository;
    private final FxDealMapper mapper;
    private final Validator validator;

    @Override
    public List<FxDealResponse> importDeals(List<FxDealRequest> dealRequests) {
        if (dealRequests == null || dealRequests.isEmpty()) {
            log.warn("Received empty or null deal request list");
            return Collections.emptyList();
        }

        log.info("Starting import of {} deals", dealRequests.size());

        List<FxDealResponse> dealResponses = new ArrayList<>();

        for (FxDealRequest request : dealRequests) {

            if (request == null) {
                continue;
            }

            Set<ConstraintViolation<FxDealRequest>> violations = validator.validate(request);
            if (!violations.isEmpty()) {
                String errorMsg = violations.stream()
                        .map(ConstraintViolation::getMessage)
                        .collect(Collectors.joining(", "));

                log.warn("Validation failed for Deal ID: {}", request.getDealUniqueId());
                dealResponses.add(createResponse(request.getDealUniqueId(), false, "Validation Error: " + errorMsg));
                continue;
            }

            try {

                if (repository.existsById(request.getDealUniqueId())) {
                    log.warn("Skipping duplicate Deal ID: {}", request.getDealUniqueId());
                    dealResponses.add(createResponse(request.getDealUniqueId(), false, "Duplicate Deal ID"));
                    continue;
                }

                FxDeal deal = mapper.requestToEntity(request);
                repository.save(deal);

                log.info("Successfully imported Deal ID: {}", request.getDealUniqueId());
                dealResponses.add(createResponse(request.getDealUniqueId(), true, "Successfully imported"));

            } catch (Exception e) {
                log.error("System Error for Deal ID: {}", request.getDealUniqueId(), e);
                dealResponses.add(createResponse(request.getDealUniqueId(), false, "System Error: " + e.getMessage()));
            }
        }
        return dealResponses;
    }

    private FxDealResponse createResponse(String id, boolean success, String message) {
        return FxDealResponse.builder()
                .dealUniqueId(id)
                .success(success)
                .message(message)
                .build();
    }
}
