package com.bloomberg.fxware.repositories;

import com.bloomberg.fxware.entities.FxDeal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FxDealRepo extends JpaRepository<FxDeal, String> {
}
