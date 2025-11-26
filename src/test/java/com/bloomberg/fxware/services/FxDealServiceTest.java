package com.bloomberg.fxware.services;

import com.bloomberg.fxware.dtos.FxDealRequest;
import com.bloomberg.fxware.dtos.FxDealResponse;
import com.bloomberg.fxware.entities.FxDeal;
import com.bloomberg.fxware.mappers.FxDealMapper;
import com.bloomberg.fxware.repositories.FxDealRepo;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class FxDealServiceTest {
    @Mock
    private FxDealRepo repository;

    @Mock
    private Validator validator;

    @Spy
    private FxDealMapper mapper = Mappers.getMapper(FxDealMapper.class);

    @InjectMocks
    private FxDealServiceImpl service;

    @BeforeEach
    void setUp() {
        lenient().when(validator.validate(any())).thenReturn(Collections.emptySet());
    }

    @Test
    void shouldHandleValidAndDuplicateDeals() {
        FxDealRequest validReq = new FxDealRequest(
                "ID_1", "USD", "EUR", LocalDateTime.now(), BigDecimal.TEN
        );
        FxDealRequest duplicateReq = new FxDealRequest(
                "ID_2", "USD", "EUR", LocalDateTime.now(), BigDecimal.TEN
        );

        List<FxDealRequest> batch = List.of(validReq, duplicateReq);

        when(repository.existsById("ID_1")).thenReturn(false);

        when(repository.existsById("ID_2")).thenReturn(true);

        when(repository.save(argThat(deal ->
                "ID_1".equals(deal.getDealUniqueId())
        ))).thenReturn(new FxDeal());

        List<FxDealResponse> results = service.importDeals(batch);

        assertEquals(2, results.size());

        assertTrue(results.get(0).isSuccess());
        assertEquals("ID_1", results.get(0).getDealUniqueId());

        assertFalse(results.get(1).isSuccess());
        assertEquals("Duplicate Deal ID", results.get(1).getMessage());

        // Verify that we only attempted to save ONCE (ID_1)
        verify(repository, times(1)).save(any(FxDeal.class));

        // Verify we checked for existence twice
        verify(repository, times(2)).existsById(anyString());
    }

    @Test
    void shouldHandleValidationErrors() {
        FxDealRequest invalidReq = new FxDealRequest(
                "ID_INVALID", null, "EUR", LocalDateTime.now(), BigDecimal.TEN
        );

        ConstraintViolation<FxDealRequest> violation = mock(ConstraintViolation.class);

        when(violation.getMessage()).thenReturn("From Currency cannot be null");

        when(validator.validate(argThat((FxDealRequest req) -> "ID_INVALID".equals(req.getDealUniqueId()))))
                .thenReturn(Set.of(violation));

        List<FxDealResponse> results = service.importDeals(List.of(invalidReq));

        assertEquals(1, results.size());

        assertFalse(results.get(0).isSuccess());

        String msg = results.get(0).getMessage();
        assertTrue(msg.contains("Validation Error"));
        assertTrue(msg.contains("From Currency cannot be null"));

        // Verify DB was never touched
        verify(repository, never()).save(any());
        verify(repository, never()).existsById(any());
    }

    @Test
    void shouldReturnEmptyWhenInputListIsNull() {
        // Act
        List<FxDealResponse> results = service.importDeals(null);

        // Assert
        assertNotNull(results);
        assertTrue(results.isEmpty());

        // Verify no dependencies were touched
        verifyNoInteractions(repository, validator, mapper);
    }

    @Test
    void shouldReturnEmptyWhenInputListIsEmpty() {
        List<FxDealResponse> results = service.importDeals(Collections.emptyList());

        assertNotNull(results);
        assertTrue(results.isEmpty());

        verifyNoInteractions(repository, validator, mapper);
    }

    @Test
    void shouldSkipNullRequestsInsideList() {
        FxDealRequest validReq = new FxDealRequest("ID_1", "USD", "EUR", LocalDateTime.now(), BigDecimal.TEN);

        List<FxDealRequest> batch = new java.util.ArrayList<>();
        batch.add(null);     // The null item to skip
        batch.add(validReq); // The valid item to process

        when(repository.existsById("ID_1")).thenReturn(false);

        List<FxDealResponse> results = service.importDeals(batch);

        assertEquals(1, results.size());
        assertEquals("ID_1", results.get(0).getDealUniqueId());
        assertTrue(results.get(0).isSuccess());

        // Verify interactions
        verify(validator, times(1)).validate(any());
        verify(repository, times(1)).existsById("ID_1");
    }
}
