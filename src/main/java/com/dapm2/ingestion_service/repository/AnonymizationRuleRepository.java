package com.dapm2.ingestion_service.repository;

import com.dapm2.ingestion_service.entity.AnonymizationRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AnonymizationRuleRepository extends JpaRepository<AnonymizationRule, Long> {

    // Return only the first (and only) record by dataSourceId
    Optional<AnonymizationRule> findFirstByDataSourceId(String dataSourceId);
}
