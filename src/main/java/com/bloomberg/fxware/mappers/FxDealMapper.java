package com.bloomberg.fxware.mappers;

import com.bloomberg.fxware.dtos.FxDealRequest;
import com.bloomberg.fxware.entities.FxDeal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FxDealMapper {
    @Mapping(source = "dealUniqueId", target = "dealUniqueId")
    FxDeal requestToEntity(FxDealRequest dealRequest);
}
