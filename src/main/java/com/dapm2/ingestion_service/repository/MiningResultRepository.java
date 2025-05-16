package com.dapm2.ingestion_service.repository;

import com.dapm2.ingestion_service.entity.MiningResult;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MiningResultRepository extends JpaRepository<MiningResult, Long> {
}
